package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@Import(JobRunner.class)
@ExtendWith(SpringExtension.class)
class JobRunnerTest {

    @Autowired
    private JobRunner runner;

    @Mock
    private ScheduledJob scheduledJob;

    @Test
    void testRun() {
        when(scheduledJob.canBeStarted()).thenReturn(true);
        JobDto job = new JobDto("name", "id", scheduledJob);

        ActionResultDto<Void> result = runner.run(job);

        TestUtils.testActionResult(result);
        verify(scheduledJob).doJob(job);
        verify(scheduledJob, never()).doJobRunning(any());
    }

    @Test
    void testRunNested() {
        when(scheduledJob.canBeStarted()).thenReturn(true);
        JobDto job = new JobDto("name", "id.", scheduledJob);

        ActionResultDto<Void> result = runner.run(job);

        TestUtils.testActionResult(result);
        verify(scheduledJob).doJob(argThat(i -> i.getId().startsWith("id.") && i.getName().equals("name")
                && i.getJob().equals(scheduledJob)));
        verify(scheduledJob, never()).doJobRunning(any());
    }

    @Test
    void testRunError() {
        when(scheduledJob.canBeStarted()).thenReturn(true);
        JobDto job = new JobDto("name", "id", scheduledJob);
        doThrow(new RuntimeException("some error")).when(scheduledJob).doJob(job);

        ActionResultDto<Void> result = runner.run(job);

        TestUtils.testActionResult(result, "some error");
        verify(scheduledJob, never()).doJobRunning(any());
    }

    @Test
    void testRunNoId() {
        when(scheduledJob.canBeStarted()).thenReturn(true);
        JobDto job = new JobDto("name", null, scheduledJob);

        ActionResultDto<Void> result = runner.run(job);

        TestUtils.testActionResult(result);
        verify(scheduledJob).doJob(argThat(i -> i.getId() != null && i.getName().equals("name")
                && i.getJob().equals(scheduledJob)));
        verify(scheduledJob, never()).doJobRunning(any());
    }


    @Test
    void testRunRunning() {
        when(scheduledJob.isRunning()).thenReturn(true);
        JobDto job = new JobDto("name", "id", scheduledJob);

        ActionResultDto<Void> result = runner.run(job);

        TestUtils.testActionResult(result);
        verify(scheduledJob, never()).doJob(any());
        verify(scheduledJob).doJobRunning(job);
    }

    @Test
    void testRunNestedRunning() {
        when(scheduledJob.isRunning()).thenReturn(true);
        JobDto job = new JobDto("name", "id.", scheduledJob);

        ActionResultDto<Void> result = runner.run(job);

        TestUtils.testActionResult(result);
        verify(scheduledJob).doJobRunning(argThat(i -> i.getId().startsWith("id.") && i.getName().equals("name")
                && i.getJob().equals(scheduledJob)));
        verify(scheduledJob, never()).doJob(any());
    }

    @Test
    void testRunErrorRunning() {
        when(scheduledJob.isRunning()).thenReturn(true);
        JobDto job = new JobDto("name", "id", scheduledJob);
        doThrow(new RuntimeException("some error")).when(scheduledJob).doJobRunning(job);

        ActionResultDto<Void> result = runner.run(job);

        TestUtils.testActionResult(result, "some error");
        verify(scheduledJob, never()).doJob(any());
    }

    @Test
    void testRunNoIdRunning() {
        when(scheduledJob.isRunning()).thenReturn(true);
        JobDto job = new JobDto("name", null, scheduledJob);

        ActionResultDto<Void> result = runner.run(job);

        TestUtils.testActionResult(result);
        verify(scheduledJob).doJobRunning(argThat(i -> i.getId() != null && i.getName().equals("name")
                && i.getJob().equals(scheduledJob)));
        verify(scheduledJob, never()).doJob(any());
    }

    @Test
    void testCantRun() {
        when(scheduledJob.getName()).thenReturn("some job");
        JobDto job = new JobDto("name", "id", scheduledJob);
        doThrow(new RuntimeException("some error")).when(scheduledJob).doJobRunning(job);

        ActionResultDto<Void> result = runner.run(job);

        TestUtils.testActionResult(result, "Action [some job] can not be started!");
        verify(scheduledJob, never()).doJob(any());
    }

}