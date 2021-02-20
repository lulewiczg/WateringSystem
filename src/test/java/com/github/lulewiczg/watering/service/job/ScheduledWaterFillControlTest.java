package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.actions.*;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

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

    //    @ParameterizedTest
//    @EnumSource(value = SystemStatus.class, names = {"ERROR", "WATERING", "DRAINING"})
//    void testNotStart(SystemStatus status) {
//        when(state.getState()).thenReturn(status);
//        JobDto jobDto = new JobDto("test");
//
//        ActionResultDto<Void> result = job.run(jobDto);
//
//        TestUtils.testActionResult(result, "Action [Water fill] can not be started!");
//        verify(tanksCloseAction, never()).doAction(any(), any());
//        verify(tapsOpenAction, never()).doAction(any(), any());
//        verify(valveOpenAction, never()).doAction(any(), any());
//        verify(outputsCloseAction, never()).doAction(any(), any());
//        verify(state, never()).setState(any());
//    }
//
    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-running-finished-test.csv")
    void testRunningFinished(SystemStatus status, int minLevel, int maxLevel, Integer level) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto jobDto = new JobDto(null, "test");

        job.doJobRunning(jobDto);

        verify(runner).run("test.", tanksCloseAction, null);
        verify(state).setState(SystemStatus.IDLE);
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-running-test.csv")
    void testRunning(SystemStatus status, int minLevel, int maxLevel, Integer level) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto jobDto = new JobDto(null, "test");

        job.doJobRunning(jobDto);

        verify(runner).run("test.", tanksCloseAction, null);
        verify(state).setState(SystemStatus.IDLE);
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-ok-test.csv")
    void testLevelOk(SystemStatus status, int minLevel, int maxLevel, Integer level) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto jobDto = new JobDto(null, "test");

        job.doJob(jobDto);

        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(state, never()).setState(any());
        verify(runner, never()).run(any(), eq(tapsOpenAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
        verify(runner, never()).run(any(), eq(outputsCloseAction), any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-test.csv")
    void testFill(SystemStatus status, int minLevel, int maxLevel, int level) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
        Sensor sensor2 = new Sensor("sensor2", 1, 3, 2, RaspiPin.GPIO_03);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        JobDto jobDto = new JobDto(null, "test");

        job.doJob(jobDto);

        verify(state).setState(SystemStatus.FILLING);
        verify(runner).run("test.", outputsCloseAction, null);
        verify(runner).run("test.", tapsOpenAction, null);
        verify(runner).run("test.", valveOpenAction, valve);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
    }

}
