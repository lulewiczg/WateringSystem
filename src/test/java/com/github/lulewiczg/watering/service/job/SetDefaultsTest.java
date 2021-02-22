package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.ValveCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Autowired
    private SetDefaults job;

    @Test
    void testJob() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 10, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, false, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        Valve valve3 = new Valve("valve3", "valve3", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
        Valve valve4 = new Valve("valve4", "valve4", ValveType.OUTPUT, false, RaspiPin.GPIO_03);
        when(state.getOutputs()).thenReturn(List.of(valve3, valve4));
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto syncDto = new JobDto(null, "test");

        job.doJob(syncDto);

        verify(runner).run("test.", openAction, valve);
        verify(runner).run("test.", openAction, valve3);
        verify(runner).run("test.", closeAction, valve2);
        verify(runner).run("test.", closeAction, valve4);
    }

    @Test
    void testOpenFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 10, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, false, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        Valve valve3 = new Valve("valve3", "valve3", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
        Valve valve4 = new Valve("valve4", "valve4", ValveType.OUTPUT, false, RaspiPin.GPIO_03);
        when(state.getOutputs()).thenReturn(List.of(valve3, valve4));
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.ERROR_RESULT);
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto syncDto = new JobDto(null, "test");

        String error = assertThrows(ActionException.class, () -> job.doJob(syncDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", openAction, valve);
    }

    @Test
    void testCloseFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 10, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, false, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        Valve valve3 = new Valve("valve3", "valve3", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
        Valve valve4 = new Valve("valve4", "valve4", ValveType.OUTPUT, false, RaspiPin.GPIO_03);
        when(state.getOutputs()).thenReturn(List.of(valve3, valve4));
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.ERROR_RESULT);
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto syncDto = new JobDto(null, "test");

        String error = assertThrows(ActionException.class, () -> job.doJob(syncDto)).getLocalizedMessage();

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
}
