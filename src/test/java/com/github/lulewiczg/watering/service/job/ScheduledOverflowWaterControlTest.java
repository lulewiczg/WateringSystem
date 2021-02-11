package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.actions.TanksCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
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

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"ERROR", "WATERING"})
    void testNotStart(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        job.run();

        verify(tanksCloseAction, never()).doAction(any());
        verify(valveOpenAction, never()).doAction(any());
        verify(state, never()).setState(any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/overflow-running-test.csv")
    void testAlreadyRunning(SystemStatus status, int minLevel, int maxLevel, Integer level) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));

        job.run();

        verify(tanksCloseAction, never()).doAction(any());
        verify(valveOpenAction, never()).doAction(any());
        verify(state, never()).setState(any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/overflow-running-finished-test.csv")
    void testAlreadyRunningNotFinished(SystemStatus status, int minLevel, int maxLevel, Integer level) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));

        job.run();

        verify(tanksCloseAction).doAction(null);
        verify(state).setState(SystemStatus.IDLE);
        verify(valveOpenAction, never()).doAction(any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/overflow-ok-test.csv")
    void testOverflowOk(SystemStatus status, int minLevel, int maxLevel, Integer level) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));

        job.run();

        verify(tanksCloseAction, never()).doAction(any());
        verify(state, never()).setState(any());
        verify(valveOpenAction, never()).doAction(any());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/overflow-test.csv")
    void testOverflow(SystemStatus status, int minLevel, int maxLevel, int level) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", minLevel, maxLevel, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
        Sensor sensor2 = new Sensor("sensor", 1, 3, 2, RaspiPin.GPIO_03);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));

        job.run();

        verify(state).setState(SystemStatus.DRAINING);
        verify(tanksCloseAction, never()).doAction(null);
        verify(valveOpenAction).doAction(valve);
    }

}
