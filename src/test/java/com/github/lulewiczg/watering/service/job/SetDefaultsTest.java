package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.ValveCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(SetDefaults.class)
@PropertySource("classpath:application-testJobs.properties")
class SetDefaultsTest {

    @MockBean
    private AppState state;

    @MockBean
    private ValveOpenAction openAction;

    @MockBean
    private ValveCloseAction closeAction;

    @MockBean
    private ActionRunner runner;

    @MockBean
    private JobRunner jobRunner;

    @MockBean
    private IOService ioService;

    @Autowired
    private SetDefaults job;

    @Test
    void testJob() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, false, 1L, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 10, 90, null, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 10, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, false, false, 1L, RaspiPin.GPIO_01);
        Sensor sensor2 = new Sensor("sensor2", 10, 90, null, Address.ADDR_41, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        Valve valve3 = new Valve("valve3", "valve3", ValveType.OUTPUT, true, false, 1L, RaspiPin.GPIO_02);
        Valve valve4 = new Valve("valve4", "valve4", ValveType.OUTPUT, false, false, 1L, RaspiPin.GPIO_03);
        when(state.getOutputs()).thenReturn(List.of(valve3, valve4));
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        job.doJob(jobDto);

        verify(runner).run("test.", openAction, valve);
        verify(runner).run("test.", openAction, valve3);
        verify(runner).run("test.", closeAction, valve2);
        verify(runner).run("test.", closeAction, valve4);
        verify(ioService, times(1)).toggleOff(RaspiPin.GPIO_10);
    }

    @Test
    void testOpenFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, false, 1L, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 10, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, false, false, 1L, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        Valve valve3 = new Valve("valve3", "valve3", ValveType.OUTPUT, true, false, 1L, RaspiPin.GPIO_02);
        Valve valve4 = new Valve("valve4", "valve4", ValveType.OUTPUT, false, false, 1L, RaspiPin.GPIO_03);
        when(state.getOutputs()).thenReturn(List.of(valve3, valve4));
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.ERROR_RESULT);
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", openAction, valve);
    }

    @Test
    void testCloseFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, false, 1L, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 10, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, false, false, 1L, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        Valve valve3 = new Valve("valve3", "valve3", ValveType.OUTPUT, true, false, 1L, RaspiPin.GPIO_02);
        Valve valve4 = new Valve("valve4", "valve4", ValveType.OUTPUT, false, false, 1L, RaspiPin.GPIO_03);
        when(state.getOutputs()).thenReturn(List.of(valve3, valve4));
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.ERROR_RESULT);
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", openAction, valve);
        verify(runner).run("test.", closeAction, valve2);
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class)
    void testCanBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertTrue(job.canBeStarted());
    }

    @Test
    void testSchedule() {
        job.schedule(jobRunner);

        verify(jobRunner).run(argThat(i -> i.getId() != null && i.getName().equals(job.getName()) && i.getJob() == job));
    }

}
