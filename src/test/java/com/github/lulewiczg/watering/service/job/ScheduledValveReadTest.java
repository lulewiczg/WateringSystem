package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(ScheduledValveRead.class)
@PropertySource("classpath:application-testJobs.properties")
class ScheduledValveReadTest {

    @MockBean
    private AppState state;

    @MockBean
    private IOService ioService;

    @Autowired
    private ScheduledValveRead job;

    @MockBean
    private JobRunner jobRunner;

    @Test
    void testOk() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 10, 90, null, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, false, RaspiPin.GPIO_01);
        Sensor sensor2 = new Sensor("sensor2", 10, 90, 10, RaspiPin.GPIO_02);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        Valve valve3 = new Valve("valve3", "valve3", ValveType.OUTPUT, false, RaspiPin.GPIO_02);

        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(state.getOutputs()).thenReturn(List.of(valve3));
        when(ioService.readPin(valve.getPin())).thenReturn(true);
        when(ioService.readPin(valve2.getPin())).thenReturn(false);
        JobDto jobDto = new JobDto("test");

        job.doJob(jobDto);

        verify(ioService).readPin(valve.getPin());
        verify(ioService).readPin(valve2.getPin());
        verify(ioService).readPin(valve3.getPin());
        verify(state, never()).setState(any());
    }

    @Test
    void testNotOk() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 10, 90, null, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, false, RaspiPin.GPIO_01);
        Sensor sensor2 = new Sensor("sensor2", 10, 90, 10, RaspiPin.GPIO_02);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(ioService.readPin(valve.getPin())).thenReturn(true);
        when(ioService.readPin(valve2.getPin())).thenReturn(true);
        JobDto jobDto = new JobDto("test");

        job.doJob(jobDto);

        verify(ioService).readPin(valve.getPin());
        verify(ioService).readPin(valve2.getPin());
        verify(state).setState(SystemStatus.ERROR);
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class)
    void testCanBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertTrue(job.canBeStarted());
    }

    @Test
    void testSchedule() {
        job.schedule(jobRunner);

        verify(jobRunner).run(argThat(i -> i.getId() != null && i.getName().equals(job.getName()) && i.getJob() == job));
    }

}
