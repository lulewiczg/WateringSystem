package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.service.actions.*;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.service.job.ScheduledSensorRead;
import com.github.lulewiczg.watering.service.job.ScheduledWatering;
import com.github.lulewiczg.watering.service.job.SetDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles({"testSimple", "testSimpleJobs"})
class ActionServiceDisabledTest {

    @Autowired
    private ActionService service;

    @MockBean
    private IOService ioService;

    @Test
    void testGetActions() {
        List<String> actions = service.getActions();
        List<Class<?>> classes = List.of(EmergencyStopAction.class, OutputsCloseAction.class, OutputsOpenAction.class, TanksCloseAction.class,
                TanksOpenAction.class, ValveCloseAction.class, ValveOpenAction.class);
        assertEquals(classes.stream().map(Class::getSimpleName).map(this::deCapitalize).collect(Collectors.toList()), actions);
    }

    @Test
    void testGetJobs() {
        List<String> jobs = service.getJobs();
        List<Class<?>> classes = List.of(ScheduledWatering.class, SetDefaults.class);
        assertEquals(classes.stream().map(Class::getSimpleName).map(this::deCapitalize).collect(Collectors.toList()), jobs);
    }

    @Test
    void testRunAction() {
        assertThrows(IllegalArgumentException.class, () ->
                service.runAction(new ActionDto(deCapitalize(WaterLevelReadAction.class.getSimpleName()), "Sensor", "sensor2")));
    }

    @Test
    void testRunJob() {
        assertThrows(IllegalArgumentException.class, () -> service.runJob(deCapitalize(ScheduledSensorRead.class.getSimpleName())));
    }

    private String deCapitalize(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}