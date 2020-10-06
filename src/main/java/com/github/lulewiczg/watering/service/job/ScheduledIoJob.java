package com.github.lulewiczg.watering.service.job;

/**
 * Abstract class for scheduled IO job.
 */
public abstract class ScheduledIoJob extends ScheduledJob{

    @Override
    protected boolean canBeStarted() {
        return true;
    }

    @Override
    protected boolean isRunning() {
        return false;
    }
}
