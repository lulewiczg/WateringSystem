package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.EmergencyStopAction;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(ScheduledWaterEscapeControl.class)
@PropertySource("classpath:application-testJobs.properties")
class ScheduledWaterEscapeControlTest {

    @MockBean
    private EmergencyStopAction emergencyStopAction;

    @MockBean
    private AppState state;

    @MockBean
    private ActionRunner runner;

    @MockBean
    private JobRunner jobRunner;

    @Autowired
    private ScheduledWaterEscapeControl job;

    @AfterEach
    void after(){
        verifyNoInteractions(emergencyStopAction);
    }

    @DirtiesContext
    @ParameterizedTest
    @CsvFileSource(resources = "/testData/leak-ok-test.csv")
    void testLeakOk(Integer level, Integer level2) {
        Valve valve = new Valve("valve2", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 0, 100, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", emergencyStopAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto(null, "test");

        job.doJob(jobDto);

        verify(runner, never()).run(any(), eq(emergencyStopAction), any());
        verify(state, never()).setState(any());

        sensor.setLevel(level2);
        job.doJob(jobDto);

        verify(runner, never()).run(any(), eq(emergencyStopAction), any());
        verify(state, never()).setState(any());
    }

    @DirtiesContext
    @ParameterizedTest
    @CsvFileSource(resources = "/testData/leak-test.csv")
    void testLeak(int level, int level2) {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 0, 100, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor2 = new Sensor("sensor2", 0, 100, 50, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(runner.run("test.", emergencyStopAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto(null, "test");

        job.doJob(jobDto);

        verify(runner, never()).run(any(), eq(emergencyStopAction), isNull());
        verify(state, never()).setState(any());

        sensor.setLevel(level2);

        job.doJob(jobDto);

        verify(state).setState(SystemStatus.ERROR);
        verify(runner).run("test.", emergencyStopAction, null);
    }

    @Test
    @DirtiesContext
    void testWaterUse() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 0, 90, 90, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", emergencyStopAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto(null, "test");

        job.doJob(jobDto);
        job.doJob(jobDto);

        sensor.setLevel(89);
        job.doJob(jobDto);

        verify(runner).run("test.", emergencyStopAction, null);
        verify(state).setState(SystemStatus.ERROR);
    }

    @Test
    @DirtiesContext
    void testNestedFail() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 0, 90, 90, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        when(runner.run("test.", emergencyStopAction, null)).thenThrow(new ActionException("id", "error"));
        JobDto jobDto = new JobDto(null, "test");
        sensor.setLevel(90);
        job.doJob(jobDto);
        sensor.setLevel(89);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.ERROR);
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"IDLE", "FILLING"})
    void testCanBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertTrue(job.canBeStarted());
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"WATERING", "DRAINING", "ERROR"})
    void testCanNotBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertFalse(job.canBeStarted());
    }
}
