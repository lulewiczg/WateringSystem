package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
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

    @BeforeEach
    void before() {
        TestUtils.standardMock(state);
    }

    @Test
    void testOk() {
        when(ioService.readPin(TestUtils.Objects.VALVE.getPin())).thenReturn(true);
        when(ioService.readPin(TestUtils.Objects.VALVE2.getPin())).thenReturn(true);
        when(ioService.readPin(TestUtils.Objects.OUT.getPin())).thenReturn(false);
        when(ioService.readPin(TestUtils.Objects.TAP_VALVE.getPin())).thenReturn(false);

        JobDto jobDto = new JobDto("test");

        job.doJob(jobDto);

        verify(ioService).readPin(TestUtils.Objects.VALVE.getPin());
        verify(ioService).readPin(TestUtils.Objects.VALVE2.getPin());
        verify(ioService).readPin(TestUtils.Objects.OUT.getPin());
        verify(ioService).readPin(TestUtils.Objects.TAP_VALVE.getPin());
        verify(state, never()).setState(any());
    }

    @Test
    void testNotOk() {
        when(ioService.readPin(TestUtils.Objects.VALVE.getPin())).thenReturn(true);
        when(ioService.readPin(TestUtils.Objects.VALVE2.getPin())).thenReturn(false);
        JobDto jobDto = new JobDto("test");

        job.doJob(jobDto);

        verify(ioService).readPin(TestUtils.Objects.VALVE.getPin());
        verify(ioService).readPin(TestUtils.Objects.VALVE2.getPin());
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
