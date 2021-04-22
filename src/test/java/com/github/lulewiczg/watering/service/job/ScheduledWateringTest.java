package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.*;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(ScheduledWatering.class)
@PropertySource("classpath:application-testJobs.properties")
class ScheduledWateringTest {

    @MockBean
    private AppState state;

    @MockBean
    private TanksOpenAction tanksOpenAction;

    @MockBean
    private TanksCloseAction tanksCloseAction;

    @MockBean
    private ValveOpenAction valveOpenAction;

    @MockBean
    private ValveCloseAction valveCloseAction;

    @MockBean
    private ActionRunner runner;

    @MockBean
    private JobRunner jobRunner;

    @Autowired
    private ScheduledWatering job;

    private final Valve out = new Valve("out", "out", ValveType.OUTPUT, true, false, 1L, RaspiPin.GPIO_10);


    @AfterEach
    void after() {
        verifyNoInteractions(tanksCloseAction);
        verifyNoInteractions(tanksOpenAction);
        verifyNoInteractions(valveOpenAction);
        verifyNoInteractions(valveCloseAction);
    }

    @Test
    void testWateringOk() throws InterruptedException {
        Valve valve = new Valve("valve", "valve", ValveType.INPUT, true, false, null, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 100, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.INPUT, true, false, null, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(state.getOutputs()).thenReturn(List.of(out));
        when(runner.run("test.", valveOpenAction, out)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveCloseAction, out)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        job.doJob(jobDto);

        verify(state).setState(SystemStatus.WATERING);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(valveCloseAction), any());
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", valveOpenAction, out);

        Thread.sleep(1500);

        verify(state).setState(SystemStatus.IDLE);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", valveCloseAction, out);
    }

    @Test
    void testWateringTanksOpenFail() {
        Valve valve = new Valve("valve", "valve", ValveType.INPUT, true, false, null, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 100, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.INPUT, true, false, null, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(state.getOutputs()).thenReturn(List.of(out));
        when(runner.run("test.", valveOpenAction, out)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveCloseAction, out)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.ERROR_RESULT);
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(valveCloseAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
    }

    @Test
    void testWateringValveOpenFail() {
        Valve valve = new Valve("valve", "valve", ValveType.INPUT, true, false, null, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 100, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.INPUT, true, false, null, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(state.getOutputs()).thenReturn(List.of(out));
        when(runner.run("test.", valveOpenAction, out)).thenReturn(TestUtils.ERROR_RESULT);
        when(runner.run("test.", valveCloseAction, out)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", valveOpenAction, out);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(valveCloseAction), any());
    }

    @Test
    void testWateringTanksCloseFail() throws InterruptedException {
        Valve valve = new Valve("valve", "valve", ValveType.INPUT, true, false, 1L, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 100, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.INPUT, true, false, 1L, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(state.getOutputs()).thenReturn(List.of(out));
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveOpenAction, out)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveCloseAction, out)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.ERROR_RESULT);
        JobDto jobDto = new JobDto("test", null);

        job.doJob(jobDto);

        Thread.sleep(1500);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", valveOpenAction, out);
        verify(runner).run("test.", valveCloseAction, out);
        verify(runner).run("test.", tanksCloseAction, null);
    }

    @Test
    void testWateringValveCloseFail() throws InterruptedException {
        Valve valve = new Valve("valve", "valve", ValveType.INPUT, true, false, 1L, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 100, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.INPUT, true, false, 1L, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(state.getOutputs()).thenReturn(List.of(out));
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveOpenAction, out)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveCloseAction, out)).thenReturn(TestUtils.ERROR_RESULT);
        JobDto jobDto = new JobDto("test", null);

        job.doJob(jobDto);

        Thread.sleep(1500);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", valveOpenAction, out);
        verify(runner).run("test.", valveCloseAction, out);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"IDLE", "DRAINING"})
    void testCanBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertTrue(job.canBeStarted());
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"WATERING", "ERROR", "FILLING"})
    void testCanNotBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertFalse(job.canBeStarted());
    }

    @Test
    void testSchedule() {
        job.schedule(jobRunner);

        verify(jobRunner).run(argThat(i -> i.getId() != null && i.getName().equals(job.getName()) && i.getJob() == job));
    }

}
