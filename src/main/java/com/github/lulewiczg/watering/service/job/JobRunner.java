package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.exception.ActionNotStartedException;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.lulewiczg.watering.service.job.ScheduledJob.SCHED;

/**
 * Runner for jobs.
 */
@Log4j2
@Component
public class JobRunner {

    /**
     * Runs job.
     *
     * @param originalJobDto job DTO
     * @return action result
     */
    public ActionResultDto<Void> run(@NonNull JobDto originalJobDto) {
        JobDto jobDto = originalJobDto.toBuilder().build();
        generateUuid(jobDto);
        String id = jobDto.getId();
        log.debug("Staring {} job with ID {} ...", jobDto.getName(), id);
        try {
            tryRun(jobDto);
        } catch (ActionNotStartedException e) {
            if (id.startsWith(SCHED)) {
                String error = String.format("Scheduled job [%s] could not be started!", jobDto.getName());
                log.error(error);
                return buildErrorMsg(jobDto, id, error);
            }
            return handleJobError(jobDto, id, e);
        } catch (Exception e) {
            return handleJobError(jobDto, id, e);
        }
        return ActionResultDto.<Void>builder()
                .id(id)
                .actionName(jobDto.getName())
                .execDate(LocalDateTime.now())
                .build();
    }

    private ActionResultDto<Void> handleJobError(JobDto jobDto, String id, Exception e) {
        log.error(String.format("Job %s failed", id), e);
        String message = e.getMessage();
        if (message == null) {
            message = "Unknown error!";
        }
        return buildErrorMsg(jobDto, id, message);
    }

    private ActionResultDto<Void> buildErrorMsg(JobDto jobDto, String id, String message) {
        return ActionResultDto.<Void>builder()
                .id(id)
                .actionName(jobDto.getName())
                .execDate(LocalDateTime.now())
                .errorMsg(message)
                .build();
    }

    private void tryRun(JobDto job) {
        ScheduledJob scheduledJob = job.getJob();
        if (scheduledJob.canBeStarted()) {
            log.debug("Job {} with ID {} can start!", scheduledJob.getName(), job.getId());
            scheduledJob.doJob(job);
        } else if (scheduledJob.isRunning()) {
            log.debug("Job {} is already running.", scheduledJob.getName());
            scheduledJob.doJobRunning(job);
        } else {
            throw new ActionNotStartedException(scheduledJob.getName());
        }
        log.debug("Finishing {} job with ID {}...", scheduledJob.getName(), job.getId());
    }

    /**
     * Generates UUID if required
     *
     * @param jobDto job DTO
     */
    private void generateUuid(JobDto jobDto) {
        String id = jobDto.getId();
        if (id == null) {
            log.debug("No UUID passed, generating new");
            jobDto.setId(UUID.randomUUID().toString());
        } else if (id.endsWith(".")) {
            log.debug("Nested invocation, appending new id...");
            jobDto.appendId(UUID.randomUUID().toString());
        }
    }

}
