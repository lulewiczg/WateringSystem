package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.TemperatureReadAction;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(ScheduledTemperatureRead.class)
@PropertySource("classpath:application-testJobs.properties")
class ScheduledTemperatureReadTest {

    @MockitoBean
    private AppState state;

    @MockitoBean
    private JobRunner jobRunner;

    @MockitoBean
    private ActionRunner runner;

    @MockitoBean
    private TemperatureReadAction temperatureReadAction;

    @MockitoSpyBean
    private ScheduledTemperatureRead job;

    @Test
    void testJob() {
        when(runner.run("test.", temperatureReadAction, null)).thenReturn(new ActionResultDto<>("id", null, BigDecimal.valueOf(69.1), LocalDateTime.now(), null));
        JobDto jobDto = new JobDto("test", null);

        job.doJob(jobDto);

        verify(runner).run("test.", temperatureReadAction, null);
        verifyNoMoreInteractions(runner);
    }

    @Test
    void testReadFail() {
        when(runner.run("test.", temperatureReadAction, null)).thenReturn(new ActionResultDto<>("id", null, null, LocalDateTime.now(), "error"));
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", temperatureReadAction, null);
        verifyNoMoreInteractions(runner);
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