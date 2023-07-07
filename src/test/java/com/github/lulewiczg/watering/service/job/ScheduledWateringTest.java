package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.WateringAction;
import com.github.lulewiczg.watering.service.actions.dto.WateringDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(ScheduledWatering.class)
@PropertySource("classpath:application-testJobs.properties")
class ScheduledWateringTest {

    @MockBean
    private AppState state;

    @MockBean
    private WateringAction wateringAction;

    @MockBean
    private ActionRunner runner;

    @MockBean
    private JobRunner jobRunner;

    @Autowired
    private ScheduledWatering job;

    @AfterEach
    void after() {
        verifyNoInteractions(wateringAction);
    }

    @BeforeEach
    void before() {
        TestUtils.standardMock(state);
    }

    @Test
    void testWateringOk() {
        Valve valve = new Valve("no", "pls no", ValveType.OUTPUT, false, false, null, RaspiPin.GPIO_11);
        when(state.getOutputs()).thenReturn(List.of(TestUtils.Objects.OUT, TestUtils.Objects.OUT2, valve));
        when(runner.run(any(), eq(wateringAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        job.doJob(jobDto);

        ArgumentCaptor<WateringDto> captor = ArgumentCaptor.forClass(WateringDto.class);
        verify(runner, times(2)).run(eq("test."), eq(wateringAction), captor.capture());
        List<WateringDto> values = captor.getAllValues();
        assertEquals(2, values.size());
        WateringDto value = values.get(0);
        WateringDto wateringDto = new WateringDto(TestUtils.Objects.OUT.getId(), TestUtils.Objects.OUT, 1, null);
        assertEquals(wateringDto, value);

        WateringDto value2 = values.get(1);
        WateringDto wateringDto2 = new WateringDto(TestUtils.Objects.OUT2.getId(), TestUtils.Objects.OUT2, 2, null);
        assertEquals(wateringDto2, value2);

        verifyNoMoreInteractions(runner);
    }

    @Test
    void testWateringTanksActionFail() {
        when(runner.run(any(), eq(wateringAction), any())).thenReturn(TestUtils.ERROR_RESULT);
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        ArgumentCaptor<WateringDto> captor = ArgumentCaptor.forClass(WateringDto.class);
        verify(runner).run(eq("test."), eq(wateringAction), captor.capture());
        WateringDto value = captor.getValue();
        WateringDto wateringDto = new WateringDto(TestUtils.Objects.OUT.getId(), TestUtils.Objects.OUT, 1, null);
        assertEquals(wateringDto, value);
        verifyNoMoreInteractions(runner);
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"IDLE", "DRAINING"})
    void testCanBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertTrue(job.canBeStarted());
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class, names = {"WATERING", "ERROR", "FILLING"})
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
