package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.actions.TanksCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.RaspiPin;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Autowired
    private ScheduledOverflowWaterControl job;

//    @ParameterizedTest
//    @EnumSource(value = SystemStatus.class, names = {"ERROR", "WATERING"})
//    void testNotStart(SystemStatus status) {
//        when(state.getState()).thenReturn(status);
//        JobDto jobDto = new JobDto("test");
//
//        ActionResultDto<Void> result = job.run(jobDto);
//
//        TestUtils.testActionResult(result, "Action [Water overflow control] can not be started!");
//        verify(tanksCloseAction, never()).doAction(any(), any());
//        verify(valveOpenAction, never()).doAction(any(), any());
//        verify(state, never()).setState(any());
//    }
//
//    @ParameterizedTest
//    @EnumSource(value = SystemStatus.class,names = {"IDLE", "DRAINING", "FILLING"})
//    void testWithId(SystemStatus status) {
//        when(state.getState()).thenReturn(status);
//        JobDto jobDto = new JobDto("test", UUID.randomUUID().toString());
//
//        ActionResultDto<Void> result = job.run(jobDto);
//
//        TestUtils.testActionResult(result);
//        assertEquals(jobDto.getId(), result.getId());
//    }
//
//    @ParameterizedTest
//    @CsvFileSource(resources = "/testData/overflow-running-test.csv")
//    void testAlreadyRunning(SystemStatus status, int minLevel, int maxLevel, Integer level) {
//        when(state.getState()).thenReturn(status);
//        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
//        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
//        Tank tank = new Tank("tank", 100, sensor, valve);
//        when(state.getTanks()).thenReturn(List.of(tank));
//        JobDto jobDto = new JobDto("test");
//
//        ActionResultDto<Void> result = job.run(jobDto);
//
//        TestUtils.testActionResult(result);
//        verify(tanksCloseAction, never()).doAction(any(), any());
//        verify(valveOpenAction, never()).doAction(any(), any());
//        verify(state, never()).setState(any());
//    }
//
//    @ParameterizedTest
//    @CsvFileSource(resources = "/testData/overflow-running-finished-test.csv")
//    void testAlreadyRunningNotFinished(SystemStatus status, int minLevel, int maxLevel, Integer level) {
//        when(state.getState()).thenReturn(status);
//        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
//        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
//        Tank tank = new Tank("tank", 100, sensor, valve);
//        when(state.getTanks()).thenReturn(List.of(tank));
//        JobDto syncDto = new JobDto("test");
//
//        ActionResultDto<Void> result = job.run(syncDto);
//
//        TestUtils.testActionResult(result);
//        verify(tanksCloseAction).doAction(new ActionDto(), null);
//        verify(state).setState(SystemStatus.IDLE);
//        verify(valveOpenAction, never()).doAction(any(), any());
//    }
//
//    @ParameterizedTest
//    @CsvFileSource(resources = "/testData/overflow-ok-test.csv")
//    void testOverflowOk(SystemStatus status, int minLevel, int maxLevel, Integer level) {
//        when(state.getState()).thenReturn(status);
//        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
//        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
//        Tank tank = new Tank("tank", 100, sensor, valve);
//        when(state.getTanks()).thenReturn(List.of(tank));
//        JobDto jobDto = new JobDto("test");
//
//        ActionResultDto<Void> result = job.run(jobDto);
//
//        TestUtils.testActionResult(result);
//        verify(tanksCloseAction, never()).doAction(any(), any());
//        verify(state, never()).setState(any());
//        verify(valveOpenAction, never()).doAction(any(), any());
//    }
//
//    @ParameterizedTest
//    @CsvFileSource(resources = "/testData/overflow-test.csv")
//    void testOverflow(SystemStatus status, int minLevel, int maxLevel, int level) {
//        when(state.getState()).thenReturn(status);
//        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
//        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
//        Tank tank = new Tank("tank", 100, sensor, valve);
//        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
//        Sensor sensor2 = new Sensor("sensor", 1, 3, 2, RaspiPin.GPIO_03);
//        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
//        when(state.getTanks()).thenReturn(List.of(tank, tank2));
//        JobDto jobDto = new JobDto("test");
//        ActionDto actionDto = jobDto.toAction();
//        when(valveOpenAction.doAction(argThat(i -> i.getId() != null), isNull())).thenCallRealMethod();
//        when(tanksCloseAction.doAction(argThat(i -> i.getId() != null), isNull())).thenCallRealMethod();
//
//        ActionResultDto<Void> result = job.run(jobDto);
//
//        TestUtils.testActionResult(result);
//        verify(state).setState(SystemStatus.DRAINING);
//        verify(tanksCloseAction, never()).doAction(any(), any());
//        verify(valveOpenAction).doAction(argThat(i -> i.getId() != null), eq(valve));
//    }
//
//    @Test
//    void testActionFail() {
//        when(state.getState()).thenReturn(SystemStatus.IDLE);
//        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
//        Sensor sensor = new Sensor("sensor", 1, 11, 20, RaspiPin.GPIO_01);
//        Tank tank = new Tank("tank", 100, sensor, valve);
//        when(state.getTanks()).thenReturn(List.of(tank));
//        doThrow(new IllegalArgumentException("error")).when(valveOpenAction).doAction(any(), eq(valve));
//        JobDto jobDto = new JobDto("test");
//
//        ActionResultDto<Void> result = job.run(jobDto);
//
//        TestUtils.testActionResult(result, "error");
//    }

}
