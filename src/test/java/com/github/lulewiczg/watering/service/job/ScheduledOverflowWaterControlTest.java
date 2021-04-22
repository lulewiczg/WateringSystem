package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.TanksCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
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
@Import(ScheduledOverflowWaterControl.class)
@PropertySource("classpath:application-testJobs.properties")
class ScheduledOverflowWaterControlTest {

    @MockBean
    private TanksCloseAction tanksCloseAction;

    @MockBean
    private ValveOpenAction valveOpenAction;

    @MockBean
    private ValveCloseAction valveCloseAction;

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
        verifyNoInteractions(valveCloseAction);
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
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.VALVE);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(state.getOverflowValves()).thenReturn(List.of(TestUtils.OUT));
        JobDto jobDto = new JobDto("test", null);

        job.doJobRunning(jobDto);

        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(valveCloseAction), any());
        verify(state, never()).setState(any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/overflow-ok-test.csv")
    void testAlreadyRunningNotFinished(int minLevel, int maxLevel, Integer level) {
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.VALVE);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(state.getOverflowValves()).thenReturn(List.of(TestUtils.OUT));
        JobDto jobDto = new JobDto("test", null);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveCloseAction, TestUtils.OUT)).thenReturn(TestUtils.EMPTY_RESULT);

        job.doJobRunning(jobDto);

        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner).run("test.", valveCloseAction, TestUtils.OUT);
        verify(runner, never()).run("test.", valveCloseAction, TestUtils.VALVE);

        verify(state).setState(SystemStatus.IDLE);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/overflow-ok-test.csv")
    void testOverflowOk(int minLevel, int maxLevel, Integer level) {
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.VALVE);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(state.getOverflowValves()).thenReturn(List.of(TestUtils.OUT));
        JobDto jobDto = new JobDto("test", null);

        job.doJob(jobDto);

        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(valveCloseAction), any());
        verify(state, never()).setState(any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/overflow-test.csv")
    void testOverflow(int minLevel, int maxLevel, int level) {
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, Address.ADDR_40, RaspiPin.GPIO_10, 10, 12, 100, 200);
        Tank tank = new Tank("tank", 100, sensor, TestUtils.VALVE);
        when(state.getTanks()).thenReturn(List.of(tank, TestUtils.TANK2));
        when(state.getOverflowValves()).thenReturn(List.of(TestUtils.OUT));
        JobDto jobDto = new JobDto("test", null);
        when(runner.run("test.", valveOpenAction, TestUtils.OUT)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveOpenAction, TestUtils.VALVE)).thenReturn(TestUtils.EMPTY_RESULT);

        job.doJob(jobDto);

        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner).run("test.", valveOpenAction, TestUtils.OUT);
        verify(runner).run("test.", valveOpenAction, TestUtils.VALVE);
        verify(runner, never()).run("test.", valveOpenAction, TestUtils.VALVE2);
        verify(runner, never()).run(any(), eq(valveCloseAction), any());
        verify(state).setState(SystemStatus.DRAINING);
    }

    @Test
    void testActionValveOpenFail() {
        when(state.getTanks()).thenReturn(List.of(TestUtils.OVERFLOW_TANK));
        when(runner.run("test.", valveOpenAction, TestUtils.VALVE)).thenReturn(TestUtils.ERROR_RESULT);
        when(state.getOverflowValves()).thenReturn(List.of(TestUtils.OUT));
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(valveCloseAction), any());
    }

    @Test
    void testActionValveCloseFail() {
        when(state.getTanks()).thenReturn(List.of(TestUtils.TANK));
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveCloseAction, TestUtils.OUT)).thenReturn(TestUtils.ERROR_RESULT);
        when(state.getOverflowValves()).thenReturn(List.of(TestUtils.OUT));
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJobRunning(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
    }

    @Test
    void testActionTanksCloseFail() {
        when(state.getTanks()).thenReturn(List.of(TestUtils.TANK));
        when(state.getOverflowValves()).thenReturn(List.of(TestUtils.OUT));
        JobDto jobDto = new JobDto("test", null);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.ERROR_RESULT);

        String error = assertThrows(ActionException.class, () -> job.doJobRunning(jobDto)).getLocalizedMessage();
        assertEquals("Action [id] failed: error", error);

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

    @Test
    void testSchedule() {
        job.schedule(jobRunner);

        verify(jobRunner).run(argThat(i -> i.getId() != null && i.getName().equals(job.getName()) && i.getJob() == job));
    }
}
