package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.WaterLevelReadAction;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(ScheduledSensorRead.class)
@PropertySource("classpath:application-testJobs.properties")
class ScheduledSensorReadTest {

    @MockBean
    private AppState state;

    @MockBean
    private WaterLevelReadAction readAction;

    @MockBean
    private ActionRunner runner;

    @MockBean
    private JobRunner jobRunner;

    @Autowired
    private ScheduledSensorRead job;

    @AfterEach
    void after() {
        verifyNoInteractions(readAction);
    }

    @BeforeEach
    void before() {
        TestUtils.standardMock(state);
    }

    @Test
    void testJob() {
        JobDto jobDto = new JobDto("test", null);
        when(runner.run("test.", readAction, TestUtils.Objects.SENSOR))
                .thenReturn(new ActionResultDto<>(UUID.randomUUID().toString(), null, 11.0, LocalDateTime.now(), null));
        when(runner.run("test.", readAction, TestUtils.Objects.SENSOR2))
                .thenReturn(new ActionResultDto<>(UUID.randomUUID().toString(), null, 22.0, LocalDateTime.now(), null));

        job.doJob(jobDto);

        assertEquals(11, TestUtils.Objects.SENSOR.getLevel());
        assertEquals(22, TestUtils.Objects.SENSOR2.getLevel());
    }

    @Test
    void testJobNestedError() {
        JobDto jobDto = new JobDto("test", null);
        when(runner.run("test.", readAction, TestUtils.Objects.SENSOR))
                .thenReturn(new ActionResultDto<>(UUID.randomUUID().toString(), null, 11.0, LocalDateTime.now(), null));
        when(runner.run("test.", readAction, TestUtils.Objects.SENSOR2)).thenReturn(new ActionResultDto<>("id", null, null, LocalDateTime.now(), "error"));

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", readAction, TestUtils.Objects.SENSOR);
        verify(runner).run("test.", readAction, TestUtils.Objects.SENSOR);
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
