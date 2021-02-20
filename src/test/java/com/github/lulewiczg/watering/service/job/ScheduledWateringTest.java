package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.actions.OutputsCloseAction;
import com.github.lulewiczg.watering.service.actions.OutputsOpenAction;
import com.github.lulewiczg.watering.service.actions.TanksCloseAction;
import com.github.lulewiczg.watering.service.actions.TanksOpenAction;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
//
//    @ParameterizedTest
//    @EnumSource(value = SystemStatus.class, names = {"WATERING", "ERROR", "FILLING"})
//    void testNotStart(SystemStatus status) {
//        when(state.getState()).thenReturn(status);
//        JobDto syncDto = new JobDto("test");
//
//        ActionResultDto<Void> result = job.run(syncDto);
//
//        TestUtils.testActionResult(result, "Action [Watering] can not be started!");
//        verify(tanksCloseAction, never()).doAction(any(), any());
//        verify(tanksOpenAction, never()).doAction(any(), any());
//        verify(outputsOpenAction, never()).doAction(any(), any());
//        verify(outputsCloseAction, never()).doAction(any(), any());
//        verify(state, never()).setState(any());
//    }
//
//    @ParameterizedTest
//    @EnumSource(value = SystemStatus.class, names = {"IDLE", "DRAINING"})
//    void testWateringOk(SystemStatus status) throws InterruptedException {
//        when(state.getState()).thenReturn(status);
//        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
//        Tank tank = new Tank("tank", 100, null, valve);
//        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_01);
//        Tank tank2 = new Tank("tank2", 100, null, valve2);
//        when(state.getTanks()).thenReturn(List.of(tank, tank2));
//        JobDto syncDto = new JobDto("test");
//        when(tanksOpenAction.doAction(argThat(i -> i.getId() != null), isNull())).thenCallRealMethod();
//        when(tanksCloseAction.doAction(argThat(i -> i.getId() != null), isNull())).thenCallRealMethod();
//        when(outputsOpenAction.doAction(argThat(i -> i.getId() != null), isNull())).thenCallRealMethod();
//        when(outputsCloseAction.doAction(argThat(i -> i.getId() != null), isNull())).thenCallRealMethod();
//
//        ActionResultDto<Void> result = job.run(syncDto);
//
//        TestUtils.testActionResult(result);
//        verify(state).setState(SystemStatus.WATERING);
//        verify(tanksCloseAction, never()).doAction(any(), any());
//        verify(outputsCloseAction, never()).doAction(any(), any());
//        verify(tanksOpenAction).doAction(argThat(i -> i.getId() != null), isNull());
//        verify(outputsOpenAction).doAction(argThat(i -> i.getId() != null), isNull());
//
//        Thread.sleep(1500);
//
//        verify(state).setState(SystemStatus.IDLE);
//        verify(tanksCloseAction).doAction(argThat(i -> i.getId() != null), isNull());
//        verify(outputsCloseAction).doAction(argThat(i -> i.getId() != null), isNull());
//    }
//
//    @ParameterizedTest
//    @EnumSource(value = SystemStatus.class, names = {"IDLE", "DRAINING"})
//    void testWithId(SystemStatus status) throws InterruptedException {
//        when(state.getState()).thenReturn(status);
//        JobDto jobDto = new JobDto("test", UUID.randomUUID().toString());
//
//        ActionResultDto<Void> result = job.run(jobDto);
//
//        Thread.sleep(1000);
//        TestUtils.testActionResult(result);
//        assertEquals(jobDto.getId(), result.getId());
//    }
}
