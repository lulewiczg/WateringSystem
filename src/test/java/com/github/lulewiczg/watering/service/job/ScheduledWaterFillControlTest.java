package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.*;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
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
@Import(ScheduledWaterFillControl.class)
@PropertySource("classpath:application-testJobs.properties")
class ScheduledWaterFillControlTest {

    @MockBean
    private TanksCloseAction tanksCloseAction;

    @MockBean
    private TapsOpenAction tapsOpenAction;

    @MockBean
    private ValveOpenAction valveOpenAction;

    @MockBean
    private OutputsCloseAction outputsCloseAction;

    @MockBean
    private AppState state;

    @MockBean
    private ActionRunner runner;

    @MockBean
    private JobRunner jobRunner;

    @Autowired
    private ScheduledWaterFillControl job;

    @AfterEach
    void after(){
        verifyNoInteractions(tanksCloseAction);
        verifyNoInteractions(tapsOpenAction);
        verifyNoInteractions(valveOpenAction);
        verifyNoInteractions(outputsCloseAction);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-ok-test.csv")
    void testNothingToDo(int minLevel, int maxLevel, Integer level) {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto jobDto = new JobDto(null, "test");

        job.doJob(jobDto);

        verify(state, never()).setState(any());
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-test.csv")
    void testFill(int minLevel, int maxLevel, int level) {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
        Sensor sensor2 = new Sensor("sensor2", 1, 3, 2, RaspiPin.GPIO_03);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveOpenAction, valve)).thenReturn(TestUtils.EMPTY_RESULT);

        JobDto jobDto = new JobDto(null, "test");

        job.doJob(jobDto);

        verify(state).setState(SystemStatus.FILLING);
        verify(runner).run("test.", outputsCloseAction, null);
        verify(runner).run("test.", tapsOpenAction, null);
        verify(runner).run("test.", valveOpenAction, valve);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-ok-test.csv")
    void testRunningFinished(int minLevel, int maxLevel, Integer level) {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto(null, "test");

        job.doJobRunning(jobDto);

        verify(state).setState(SystemStatus.IDLE);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-running-test.csv")
    void testRunning(int minLevel, int maxLevel, Integer level) {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto(null, "test");

        job.doJobRunning(jobDto);

        verify(state).setState(SystemStatus.IDLE);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @Test
    void testOutputsCloseFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 20, 100, 10, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
        Sensor sensor2 = new Sensor("sensor2", 1, 3, 2, RaspiPin.GPIO_03);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(runner.run("test.", tapsOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run(eq("test."), eq(valveOpenAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenThrow(new ActionException("id", "error"));
        JobDto jobDto = new JobDto(null, "test");

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.FILLING);
        verify(runner).run("test.", outputsCloseAction, null);
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
    }

    @Test
    void testTapsOpenFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 20, 100, 10, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
        Sensor sensor2 = new Sensor("sensor2", 1, 3, 2, RaspiPin.GPIO_03);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(runner.run(eq("test."), eq(valveOpenAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsOpenAction, null)).thenThrow(new ActionException("id", "error"));

        JobDto jobDto = new JobDto(null, "test");

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.FILLING);
        verify(runner).run("test.", outputsCloseAction, null);
        verify(runner).run("test.", tapsOpenAction, null);
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
    }

    @Test
    void testValveOpenFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 20, 100, 10, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
        Sensor sensor2 = new Sensor("sensor2", 1, 3, 2, RaspiPin.GPIO_03);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run(eq("test."), eq(valveOpenAction), any())).thenThrow(new ActionException("id", "error"));

        JobDto jobDto = new JobDto(null, "test");

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.FILLING);
        verify(runner).run("test.", outputsCloseAction, null);
        verify(runner).run("test.", tapsOpenAction, null);
        verify(runner).run("test.", valveOpenAction, valve);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
    }

    @Test
    void testTanksCloseFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 1, 100, 101, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", tanksCloseAction, null)).thenThrow(new ActionException("id", "error"));
        JobDto jobDto = new JobDto(null, "test");

        String error = assertThrows(ActionException.class, () -> job.doJobRunning(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state,never()).setState(any());
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"ERROR", "WATERING", "DRAINING", "FILLING"})
    void testCanBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertFalse(job.canBeStarted());
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"IDLE"})
    void testCanNotBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertTrue(job.canBeStarted());
    }

}
