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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(ScheduledWatering.class)
@PropertySource("classpath:application-testJobs.properties")
class ScheduledWateringTest {

    @Value("${com.github.lulewiczg.watering.schedule.watering.duration}")
    private Long wateringLength;

    @MockBean
    private AppState state;

    @MockBean
    private TanksOpenAction tanksOpenAction;

    @MockBean
    private TanksCloseAction tanksCloseAction;

    @MockBean
    private OutputsOpenAction outputsOpenAction;

    @MockBean
    private OutputsCloseAction outputsCloseAction;

    @MockBean
    private ActionRunner runner;

    @MockBean
    private JobRunner jobRunner;

    @Autowired
    private ScheduledWatering job;

    @AfterEach
    void after(){
        verifyNoInteractions(tanksCloseAction);
        verifyNoInteractions(tanksOpenAction);
        verifyNoInteractions(outputsOpenAction);
        verifyNoInteractions(outputsCloseAction);
    }

    @Test
    void testWateringOk() throws InterruptedException {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 100, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(runner.run("test.", outputsOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto syncDto = new JobDto(null, "test");

        job.doJob(syncDto);

        verify(state).setState(SystemStatus.WATERING);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", outputsOpenAction, null);

        Thread.sleep(1500);

        verify(state).setState(SystemStatus.IDLE);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", outputsCloseAction, null);
    }

    @Test
    void testWateringTanksOpenFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 100, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(runner.run("test.", outputsOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksOpenAction, null)).thenThrow(new ActionException("id", "error"));
        JobDto syncDto = new JobDto(null, "test");

        String error = assertThrows(ActionException.class, () -> job.doJob(syncDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(outputsOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @Test
    void testWateringOutputsOpenFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 100, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsOpenAction, null)).thenThrow(new ActionException("id", "error"));
        JobDto syncDto = new JobDto(null, "test");

        String error = assertThrows(ActionException.class, () -> job.doJob(syncDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", outputsOpenAction, null);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @Test
    void testWateringTanksCloseFail() throws InterruptedException {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 100, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenThrow(new ActionException("id", "error"));
        JobDto syncDto = new JobDto(null, "test");

        job.doJob(syncDto);

        Thread.sleep(1500);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", outputsOpenAction, null);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @Test
    void testWateringOutputsCloseFail() throws InterruptedException {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 100, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenThrow(new ActionException("id", "error"));
        JobDto syncDto = new JobDto(null, "test");

        job.doJob(syncDto);

        Thread.sleep(1500);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", outputsOpenAction, null);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", outputsCloseAction, null);
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
}
