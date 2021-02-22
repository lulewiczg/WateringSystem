package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.TanksCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
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
@Import(ScheduledOverflowWaterControl.class)
@PropertySource("classpath:application-testJobs.properties")
class ScheduledOverflowWaterControlTest {

    @MockBean
    private TanksCloseAction tanksCloseAction;

    @MockBean
    private ValveOpenAction valveOpenAction;

    @MockBean
    private AppState state;

    @MockBean
    private ActionRunner runner;

    @MockBean
    private JobRunner jobRunner;

    @Autowired
    private ScheduledOverflowWaterControl job;

    @AfterEach
    void after() {
        verifyNoInteractions(tanksCloseAction);
        verifyNoInteractions(valveOpenAction);
    }

    @Test
    void testNothingToDo() {
        JobDto jobDto = new JobDto("test");

        job.doJob(jobDto);

        verify(runner, never()).run(any(), any(), any());
        verify(state, never()).setState(any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/overflow-running-test.csv")
    void testAlreadyRunning(int minLevel, int maxLevel, Integer level) {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto jobDto = new JobDto(null, "test");

        job.doJobRunning(jobDto);

        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(state, never()).setState(any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/overflow-ok-test.csv")
    void testAlreadyRunningNotFinished(int minLevel, int maxLevel, Integer level) {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto syncDto = new JobDto(null, "test");
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);

        job.doJobRunning(syncDto);

        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(state).setState(SystemStatus.IDLE);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/overflow-ok-test.csv")
    void testOverflowOk(int minLevel, int maxLevel, Integer level) {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto jobDto = new JobDto(null, "test");

        job.doJob(jobDto);

        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(state, never()).setState(any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/overflow-test.csv")
    void testOverflow(int minLevel, int maxLevel, int level) {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
        Sensor sensor2 = new Sensor("sensor", 1, 3, 2, RaspiPin.GPIO_03);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        JobDto jobDto = new JobDto(null, "test");
        when(runner.run("test.", valveOpenAction, valve)).thenReturn(TestUtils.EMPTY_RESULT);

        job.doJob(jobDto);

        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner).run("test.", valveOpenAction, valve);
        verify(state).setState(SystemStatus.DRAINING);
    }

    @Test
    void testActionValveOpenFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 1, 11, 20, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", valveOpenAction, valve)).thenThrow(new ActionException("id", "error"));
        JobDto jobDto = new JobDto(null, "test");

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
    }

    @Test
    void testActionTanksCloseFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 1, 90, 89, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto syncDto = new JobDto(null, "test");
        when(runner.run("test.", tanksCloseAction, null)).thenThrow(new ActionException("id", "error"));

        job.doJobRunning(syncDto);

        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(state, never()).setState(any());
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"FILLING", "IDLE"})
    void testCanBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertTrue(job.canBeStarted());
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"DRAINING", "WATERING", "ERROR"})
    void testCanNotBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertFalse(job.canBeStarted());
    }


}
