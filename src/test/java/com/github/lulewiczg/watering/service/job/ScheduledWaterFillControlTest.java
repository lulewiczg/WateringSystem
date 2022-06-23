package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.*;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
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
    private TapsCloseAction tapsCloseAction;

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
    void after() {
        verifyNoInteractions(tanksCloseAction);
        verifyNoInteractions(tapsOpenAction);
        verifyNoInteractions(tanksCloseAction);
        verifyNoInteractions(valveOpenAction);
        verifyNoInteractions(outputsCloseAction);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-ok-test.csv")
    void testNothingToDo(int minLevel, int maxLevel, Integer level) {
        Sensor sensor = new Sensor("sensor", level, minLevel, maxLevel, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.Objects.VALVE, null);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto jobDto = new JobDto("test", null);

        job.doJob(jobDto);

        verify(state, never()).setState(any());
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
        verify(runner, never()).run(any(), eq(tapsCloseAction), any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-test.csv")
    void testFill(int minLevel, int maxLevel, int level) {
        Sensor sensor = new Sensor("sensor", level, minLevel, maxLevel, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.Objects.VALVE, null);
        when(state.getTanks()).thenReturn(List.of(tank, TestUtils.Objects.TANK2));
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveOpenAction, TestUtils.Objects.VALVE)).thenReturn(TestUtils.EMPTY_RESULT);

        JobDto jobDto = new JobDto("test", null);

        job.doJob(jobDto);

        verify(state).setState(SystemStatus.FILLING);
        verify(runner).run("test.", outputsCloseAction, null);
        verify(runner).run("test.", tapsOpenAction, null);
        verify(runner).run("test.", valveOpenAction, TestUtils.Objects.VALVE);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(tapsCloseAction), any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-ok-test.csv")
    void testRunningFinished(int minLevel, int maxLevel, Integer level) {
        Sensor sensor = new Sensor("sensor", level, minLevel, maxLevel, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.Objects.VALVE, null);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        job.doJobRunning(jobDto);

        verify(state).setState(SystemStatus.IDLE);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", tapsCloseAction, null);
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-running-test.csv")
    void testRunning(int minLevel, int maxLevel, Integer level) {
        Sensor sensor = new Sensor("sensor", level, minLevel, maxLevel, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.Objects.VALVE, null);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        job.doJobRunning(jobDto);

        verify(state).setState(SystemStatus.IDLE);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", tapsCloseAction, null);
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @Test
    void testOutputsCloseFail() {
        Valve valve = new Valve("valve", "valve", ValveType.INPUT, true, false, null, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 10, 20, 100, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, valve, null);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.INPUT, true, false, null, RaspiPin.GPIO_02);
        Sensor sensor2 = new Sensor("sensor2", 2, 1, 3, Address.ADDR_41, RaspiPin.GPIO_20, 10, 12, 100, 200);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2, null);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(runner.run("test.", tapsOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run(eq("test."), eq(valveOpenAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.ERROR_RESULT);
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.FILLING);
        verify(runner).run("test.", outputsCloseAction, null);
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(tapsCloseAction), any());
    }

    @Test
    void testTapsOpenFail() {
        Sensor sensor = new Sensor("sensor", 10, 20, 100, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.Objects.VALVE, null);
        when(state.getTanks()).thenReturn(List.of(tank, TestUtils.Objects.TANK2));
        when(runner.run(eq("test."), eq(valveOpenAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsOpenAction, null)).thenReturn(TestUtils.ERROR_RESULT);

        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.FILLING);
        verify(runner).run("test.", outputsCloseAction, null);
        verify(runner).run("test.", tapsOpenAction, null);
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(tapsCloseAction), any());
    }

    @Test
    void testValveOpenFail() {
        Sensor sensor = new Sensor("sensor", 10, 20, 100, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.Objects.VALVE, null);
        when(state.getTanks()).thenReturn(List.of(tank, TestUtils.Objects.TANK2));
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run(eq("test."), eq(valveOpenAction), any())).thenReturn(TestUtils.ERROR_RESULT);

        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.FILLING);
        verify(runner).run("test.", outputsCloseAction, null);
        verify(runner).run("test.", tapsOpenAction, null);
        verify(runner).run("test.", valveOpenAction, TestUtils.Objects.VALVE);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(tapsCloseAction), any());
    }

    @Test
    void testTanksCloseFail() {
        Sensor sensor = new Sensor("sensor", 101, 1, 100, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.Objects.VALVE, null);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.ERROR_RESULT);
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJobRunning(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state, never()).setState(any());
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
        verify(runner, never()).run(any(), eq(tapsCloseAction), any());
    }

    @Test
    void testTapsCloseFail() {
        Sensor sensor = new Sensor("sensor", 101, 1, 100, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.Objects.VALVE, null);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsCloseAction, null)).thenReturn(TestUtils.ERROR_RESULT);
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJobRunning(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state, never()).setState(any());
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", tapsCloseAction, null);
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @Test
    void testNoSensor() {
        Tank tank = new Tank("tank", 100, null, TestUtils.Objects.VALVE, null);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveOpenAction, TestUtils.Objects.VALVE)).thenReturn(TestUtils.EMPTY_RESULT);

        JobDto jobDto = new JobDto("test", null);

        job.doJob(jobDto);

        verify(state, never()).setState(any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(tapsCloseAction), any());
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

    @Test
    void testSchedule() {
        job.schedule(jobRunner);

        verify(jobRunner).run(argThat(i -> i.getId() != null && i.getName().equals(job.getName()) && i.getJob() == job));
    }

}
