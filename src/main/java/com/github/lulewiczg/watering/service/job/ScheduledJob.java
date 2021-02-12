package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.SystemStatus;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract class for scheduled job.
 */
@Log4j2
public abstract class ScheduledJob {

    /**
     * Action name.
     *
     * @return name
     */
    public abstract String getName();

    /**
     * Status that action uses.
     *
     * @return status
     */
    protected SystemStatus getJobStatus() {
        return null;
    }

    /**
     * Current system status.
     *
     * @return status
     */
    protected SystemStatus getState() {
        return null;
    }

    /**
     * Action logic.
     */
    protected abstract void doJob();

    /**
     * Action logic when already running.
     */
    protected void doJobRunning() {
        //Do nothing
    }

    /**
     * Checks if action can be started.
     *
     * @return true if can
     */
    public boolean canBeStarted() {
        SystemStatus state = getState();
        if (!state.canBeInterrupted(getJobStatus())) {
            log.debug("{} can not start, state {}", getName(), state);
            onStartFailed();
            return false;
        }
        return true;
    }

    /**
     * Callback when job can not be started.
     */
    protected void onStartFailed() {
        //Do nothing
    }

    /**
     * Runs job.
     *
     * @param jobDto job DTO
     * @return action result
     */
    public final ActionResultDto<Void> run(@NonNull JobDto jobDto) {
        UUID id = getUuid(jobDto.getId());
        log.debug("Staring {} job with ID {} ...", getName(), id);
        try {
            tryRun(id);
        } catch (Exception e) {
            log.error(String.format("Job %s failed", id), e);
            String message = e.getMessage();
            if (message == null) {
                message = "Unknown error!";
            }
            return new ActionResultDto<>(id, LocalDateTime.now(), message);
        }
        return new ActionResultDto<>(id, null, LocalDateTime.now());
    }

    /**
     * Runs job with new UUID.
     *
     * @return action result
     */
    public final UUID getUuid(UUID uuid) {
        if (uuid == null) {
            log.debug("No UUID passed, generating new");
            return UUID.randomUUID();
        }
        return uuid;
    }

    private void tryRun(UUID id) {
        if (canBeStarted()) {
            log.debug("Job {} with ID {} can start!", getName(), id);
            doJob();
        }
        if (isRunning()) {
            log.debug("Job {} is already running.", getName());
            doJobRunning();
        }
        log.debug("Finishing {} job with ID {}...", getName(), id);
    }

    protected boolean isRunning() {
        return getJobStatus() == getState();
    }

}
