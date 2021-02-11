package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.state.SystemStatus;
import lombok.extern.log4j.Log4j2;

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
     * Runs action.
     */
    public final void run() {
        log.debug("Staring {} job...", getName());
        if (canBeStarted()) {
            log.debug("Job {} can start!", getName());
            doJob();
        }
        if (isRunning()) {
            log.debug("Job {} is already running.", getName());
            doJobRunning();
        }
        log.debug("Finishing {} job...", getName());
    }

    protected boolean isRunning() {
        return getJobStatus() == getState();
    }
}
