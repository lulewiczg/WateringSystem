package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.exception.InvalidParamException;
import com.github.lulewiczg.watering.exception.SensorNotFoundException;
import com.github.lulewiczg.watering.service.actions.*;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.service.job.*;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles({"test", "testJobs"})
class ActionServiceTest {

    @Autowired
    private ActionService service;

    @MockBean
    private IOService ioService;

    @Test
    void testGetActions() {
        List<String> actions = service.getActions();
        List<Class<?>> classes = List.of(EmergencyStopAction.class, OutputsCloseAction.class, OutputsOpenAction.class, TanksCloseAction.class,
                TanksOpenAction.class, TapsCloseAction.class, TapsOpenAction.class, ValveCloseAction.class, ValveOpenAction.class,
                WaterLevelReadAction.class);
        assertEquals(classes.stream().map(Class::getSimpleName).map(this::deCapitalize).collect(Collectors.toList()), actions);
    }

    @Test
    void testGetJobs() {
        List<String> jobs = service.getJobs();
        List<Class<?>> classes = List.of(ScheduledOverflowWaterControl.class, ScheduledSensorRead.class, ScheduledValveRead.class,
                ScheduledWaterEscapeControl.class, ScheduledWaterFillControl.class, ScheduledWatering.class, SetDefaults.class);
        assertEquals(classes.stream().map(Class::getSimpleName).map(this::deCapitalize).collect(Collectors.toList()), jobs);
    }

    @Test
    void testRunAction() {
        service.runAction(new ActionDto(deCapitalize(WaterLevelReadAction.class.getSimpleName()), "Sensor", "sensor2"));

        verify(ioService, atLeast(1)).analogRead(RaspiPin.GPIO_02);
    }

    @Test
    void testRunActionMissingParam() {
        assertThrows(SensorNotFoundException.class, () -> service.runAction(new ActionDto(deCapitalize(WaterLevelReadAction.class.getSimpleName()), "Sensor", null)));
        ;
    }

    @Test
    void testRunActionInvalidParam() {
        assertThrows(SensorNotFoundException.class, () -> service.runAction(new ActionDto(deCapitalize(WaterLevelReadAction.class.getSimpleName()), "Sensor", "abc")));
    }

    @Test
    void testRunActionInvalidType() {
        assertThrows(InvalidParamException.class, () -> service.runAction(new ActionDto(deCapitalize(WaterLevelReadAction.class.getSimpleName()), "String", "sensor2")));
    }

    @Test
    void testRunJob() {
        service.runJob(deCapitalize(ScheduledSensorRead.class.getSimpleName()));

        verify(ioService, atLeast(1)).analogRead(RaspiPin.GPIO_01);
    }

    @Test
    void testRunJobNoName() {
        assertThrows(IllegalArgumentException.class, () -> service.runJob(null));
    }

    @Test
    void testRunJobInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> service.runJob("abc"));
    }


    private String deCapitalize(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}