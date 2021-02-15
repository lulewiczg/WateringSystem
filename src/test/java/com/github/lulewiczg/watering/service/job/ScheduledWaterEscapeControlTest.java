package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.actions.EmergencyStopAction;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    @Autowired
    private ScheduledWaterEscapeControl job;

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"WATERING", "DRAINING"})
    void testAlreadyRunning(SystemStatus status) {
        when(state.getState()).thenReturn(status);
        JobDto syncDto = new JobDto("test");

        ActionResultDto<Void> result = job.run(syncDto);

        TestUtils.testActionResult(result);
        verify(emergencyStopAction, never()).doAction(any(), any());
        verify(state, never()).setState(any());
    }

    @DirtiesContext
    @ParameterizedTest
    @CsvFileSource(resources = "/testData/leak-ok-test.csv")
    void testLeakOk(SystemStatus status, Integer level, Integer level2) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve2", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 0, 100, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto syncDto = new JobDto("test");

        ActionResultDto<Void> result = job.run(syncDto);

        TestUtils.testActionResult(result);
        verify(emergencyStopAction, never()).doAction(any(), any());
        verify(state, never()).setState(any());

        sensor.setLevel(level2);
        ActionResultDto<Void> result2 = job.run(syncDto);

        TestUtils.testActionResult(result2);
        assertNotEquals(result.getId(), result2.getId());
        verify(emergencyStopAction, never()).doAction(any(), any());
        verify(state, never()).setState(any());
    }

    @DirtiesContext
    @ParameterizedTest
    @CsvFileSource(resources = "/testData/leak-test.csv")
    void testLeak(SystemStatus status, int level, int level2) {
        when(state.getState()).thenReturn(status);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 0, 100, level, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor2 = new Sensor("sensor2", 0, 100, 50, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        JobDto syncDto = new JobDto("test");

        ActionResultDto<Void> result = job.run(syncDto);

        TestUtils.testActionResult(result);
        verify(emergencyStopAction, never()).doAction(any(), any());
        verify(state, never()).setState(any());

        sensor.setLevel(level2);

        ActionResultDto<Void> result2 = job.run(syncDto);

        TestUtils.testActionResult(result2);
        assertNotEquals(result.getId(), result2.getId());
        verify(state).setState(SystemStatus.ERROR);
        verify(emergencyStopAction).doAction(new ActionDto(), null);
    }

    @Test
    @DirtiesContext
    void testWaterUse() {
        when(state.getState()).thenReturn(SystemStatus.IDLE);
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 0, 90, 90, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        when(state.getTanks()).thenReturn(List.of(tank));
        JobDto syncDto = new JobDto("test");

        job.run(syncDto);
        job.run(syncDto);

        when(state.getState()).thenReturn(SystemStatus.WATERING);
        sensor.setLevel(89);
        job.run(syncDto);
        sensor.setLevel(80);
        job.run(syncDto);
        sensor.setLevel(70);
        job.run(syncDto);

        when(state.getState()).thenReturn(SystemStatus.DRAINING);
        sensor.setLevel(60);
        job.run(syncDto);
        sensor.setLevel(50);
        job.run(syncDto);
        sensor.setLevel(40);
        job.run(syncDto);

        when(state.getState()).thenReturn(SystemStatus.IDLE);
        job.run(syncDto);
        job.run(syncDto);

        verify(emergencyStopAction, never()).doAction(any(), any());
        verify(state, never()).setState(any());
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class)
    void testWithId(SystemStatus status) {
        when(state.getState()).thenReturn(status);
        JobDto jobDto = new JobDto("test", UUID.randomUUID().toString());

        ActionResultDto<Void> result = job.run(jobDto);

        TestUtils.testActionResult(result);
        assertEquals(jobDto.getId(), result.getId());
    }

}
