package com.github.lulewiczg.watering.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ActionNotFoundException;
import com.github.lulewiczg.watering.exception.JobNotFoundException;
import com.github.lulewiczg.watering.exception.TypeMismatchException;
import com.github.lulewiczg.watering.exception.ValueNotAllowedException;
import com.github.lulewiczg.watering.service.actions.EmergencyStopAction;
import com.github.lulewiczg.watering.service.actions.WaterLevelReadAction;
import com.github.lulewiczg.watering.service.actions.dto.WateringDto;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.service.job.*;
import com.github.lulewiczg.watering.state.AppState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"test", "testJobs"})
class ActionServiceImplIntegTest {

    @Autowired
    private ActionServiceImpl service;

    @Autowired
    private AppState state;

    @MockBean
    private IOService ioService;

    @Autowired
    private ObjectMapper mapper;

    @AfterAll
    static void after() {
        TestUtils.Objects.reset();
    }

    @Test
    void testGetActions() {
        ActionDefinitionDto[] expected = TestUtils.readJson("actions.json", ActionDefinitionDto[].class, mapper);

        List<ActionDefinitionDto> actions = service.getActions();

        assertEquals(Arrays.asList(expected), actions);
    }

    @Test
    void testGetJobs() {
        List<JobDefinitionDto> jobs = service.getJobs();
        List<JobDefinitionDto> expected = Stream.of(ScheduledOverflowWaterControl.class, ScheduledSensorRead.class, ScheduledValveRead.class,
                        ScheduledWaterEscapeControl.class, ScheduledWaterFillControl.class, ScheduledWatering.class, SetDefaults.class)
                .map(i -> new JobDefinitionDto(deCapitalize(i.getSimpleName()), true)).collect(Collectors.toList());
        assertEquals(expected, jobs);
    }


    @Test
    @DirtiesContext
    void testActions() {
        List<ActionDefinitionDto> actions = service.getActions();
        actions.forEach(i -> {
            Object param = Optional.ofNullable(i.getAllowedValues()).map(j -> j.get(0)).orElse(null);
            if (i.getParameterDestinationType() == WateringDto.class) {
                param = Map.of("valveId","valve1", "seconds", 2);
            }
            ActionDto action = new ActionDto(i.getActionName(), param);
            assertDoesNotThrow(() -> service.runAction(action));
        });
    }

    @Test
    @DirtiesContext
    void testJobs() {
        List<JobDefinitionDto> jobs = service.getJobs();
        jobs.forEach(i -> {
            JobDto action = new JobDto("test", i.getJobName());
            assertDoesNotThrow(() -> service.runJob(action));
        });
    }

    @Test
    void testRunAction() {
        service.runAction(new ActionDto(deCapitalize(WaterLevelReadAction.class.getSimpleName()), "sensor1"));

        verify(ioService, atLeast(1)).analogRead(TestUtils.Objects.SENSOR);
    }


    @Test
    void testRunInvalidAction() {
        ActionDto sensor = new ActionDto("test", "test");

        String message = assertThrows(ActionNotFoundException.class, () -> service.runAction(sensor)).getMessage();

        assertEquals("Action not found: test", message);
    }

    @Test
    void testRunActionMissingParam() {
        ActionDto sensor = new ActionDto(deCapitalize(WaterLevelReadAction.class.getSimpleName()), null);

        String message = assertThrows(TypeMismatchException.class, () -> service.runAction(sensor)).getMessage();

        assertEquals("[null] is not valid value for class java.lang.String type!", message);
    }

    @Test
    void testRunActionInvalidParam() {
        ActionDto sensor = new ActionDto(deCapitalize(WaterLevelReadAction.class.getSimpleName()), "invalid");

        String message = assertThrows(ValueNotAllowedException.class, () -> service.runAction(sensor)).getMessage();

        assertEquals("Value [invalid] does not match [sensor1, sensor2]!", message);
    }

    @Test
    void testRunActionInvalidParamType() {
        ActionDto sensor = new ActionDto(deCapitalize(WaterLevelReadAction.class.getSimpleName()), 1);

        String message = assertThrows(TypeMismatchException.class, () -> service.runAction(sensor)).getMessage();

        assertEquals("[1] is not valid value for class java.lang.String type!", message);
    }

    @Test
    void testRunActionVoidType() {
        ActionDto sensor = new ActionDto(deCapitalize(EmergencyStopAction.class.getSimpleName()), null);

        service.runAction(sensor);
    }

    @Test
    void testRunActionVoidWithParam() {
        ActionDto sensor = new ActionDto(deCapitalize(EmergencyStopAction.class.getSimpleName()), "some value");

        String message = assertThrows(TypeMismatchException.class, () -> service.runAction(sensor)).getMessage();

        assertEquals("[some value] is not valid value for class java.lang.Void type!", message);
    }

    @Test
    void testRunJob() {
        when(ioService.analogRead(TestUtils.Objects.SENSOR)).thenReturn(0.24);
        when(ioService.analogRead(TestUtils.Objects.SENSOR2)).thenReturn(0.2);

        service.runJob(new JobDto(deCapitalize(ScheduledSensorRead.class.getSimpleName())));

        assertEquals(55, state.getTanks().get(0).getSensor().getLevel());
        assertEquals(83 , state.getTanks().get(1).getSensor().getLevel());

    }

    @Test
    void testRunJobInvalidName() {
        JobDto jobDto = new JobDto("abc");
        String message = assertThrows(JobNotFoundException.class, () -> service.runJob(jobDto)).getMessage();

        assertEquals("Job not found: abc", message);
    }

    private String deCapitalize(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
