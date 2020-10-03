package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.actions.OutputsCloseAction;
import com.github.lulewiczg.watering.service.actions.OutputsOpenAction;
import com.github.lulewiczg.watering.service.actions.TanksCloseAction;
import com.github.lulewiczg.watering.service.actions.TanksOpenAction;
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
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private ScheduledWatering job;

    @Test
    void testAlreadyRunning() {
        when(state.getState()).thenReturn(SystemStatus.WATERING);

        job.run();

        verify(tanksCloseAction, never()).doAction(any());
        verify(tanksOpenAction, never()).doAction(any());
        verify(outputsOpenAction, never()).doAction(any());
        verify(outputsCloseAction, never()).doAction(any());
        verify(state, never()).setState(any());
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "WATERING")
    void testOverflowOk(SystemStatus status) throws InterruptedException {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Tank tank = new Tank(100, null, valve);
        Valve valve2 = new Valve("valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_01);
        Tank tank2 = new Tank(100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));

        job.run();

        verify(state).setState(SystemStatus.WATERING);
        verify(tanksCloseAction, never()).doAction(any());
        verify(outputsCloseAction, never()).doAction(any());
        verify(tanksOpenAction).doAction(null);
        verify(outputsOpenAction).doAction(null);

        Thread.sleep(1500);

        verify(state).setState(SystemStatus.IDLE);
        verify(tanksCloseAction).doAction(null);
        verify(outputsCloseAction).doAction(null);
    }
}