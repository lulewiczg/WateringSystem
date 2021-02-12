package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.actions.OutputsCloseAction;
import com.github.lulewiczg.watering.service.actions.TanksCloseAction;
import com.github.lulewiczg.watering.service.actions.TapsOpenAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
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

    @Autowired
    private ScheduledWaterFillControl job;

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"ERROR", "WATERING", "DRAINING"})
    void testNotStart(SystemStatus status) {
        when(state.getState()).thenReturn(status);
        JobDto jobDto = new JobDto("test");

        ActionResultDto<Void> result = job.run(jobDto);

        TestUtils.testActionResult(result);
        verify(tanksCloseAction, never()).doAction(any());
        verify(tapsOpenAction, never()).doAction(any());
        verify(valveOpenAction, never()).doAction(any());
        verify(outputsCloseAction, never()).doAction(any());
        verify(state, never()).setState(any());
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class)
    void testWithUuid(SystemStatus status) {
        when(state.getState()).thenReturn(status);
        JobDto jobDto = new JobDto("test", UUID.randomUUID());

        ActionResultDto<Void> result = job.run(jobDto);

        TestUtils.testActionResult(result);
        assertEquals(jobDto.getId(), result.getId());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-running-finished-test.csv")
    void testRunningFinished(SystemStatus status, int minLevel, int maxLevel, Integer level) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto jobDto = new JobDto("test");

        ActionResultDto<Void> result = job.run(jobDto);

        TestUtils.testActionResult(result);
        verify(tanksCloseAction).doAction(null);
        verify(state).setState(SystemStatus.IDLE);
        verify(tapsOpenAction, never()).doAction(any());
        verify(valveOpenAction, never()).doAction(any());
        verify(outputsCloseAction, never()).doAction(any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-running-test.csv")
    void testRunning(SystemStatus status, int minLevel, int maxLevel, Integer level) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto jobDto = new JobDto("test");

        ActionResultDto<Void> result = job.run(jobDto);

        TestUtils.testActionResult(result);
        verify(tanksCloseAction).doAction(null);
        verify(state).setState(SystemStatus.IDLE);
        verify(tapsOpenAction, never()).doAction(any());
        verify(valveOpenAction, never()).doAction(any());
        verify(outputsCloseAction, never()).doAction(any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/fill-ok-test.csv")
    void testLevelOk(SystemStatus status, int minLevel, int maxLevel, Integer level) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto jobDto = new JobDto("test");

        ActionResultDto<Void> result = job.run(jobDto);

        TestUtils.testActionResult(result);
        verify(tanksCloseAction, never()).doAction(any());
        verify(state, never()).setState(any());
        verify(tapsOpenAction, never()).doAction(any());
        verify(valveOpenAction, never()).doAction(any());
        verify(outputsCloseAction, never()).doAction(any());
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
        JobDto jobDto = new JobDto("test");

        ActionResultDto<Void> result = job.run(jobDto);

        TestUtils.testActionResult(result);
        verify(state).setState(SystemStatus.FILLING);
        verify(outputsCloseAction).doAction(null);
        verify(tapsOpenAction).doAction(null);
        verify(valveOpenAction).doAction(valve);
        verify(tanksCloseAction, never()).doAction(any());
    }

}
