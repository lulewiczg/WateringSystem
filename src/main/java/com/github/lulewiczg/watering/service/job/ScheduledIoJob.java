package com.github.lulewiczg.watering.service.job;

/**
 * Abstract class for scheduled IO job.
 */
public abstract class ScheduledIoJob extends ScheduledJob{

    @Override
    public boolean canBeStarted() {
        return true;
    }

    @Override
    protected boolean isRunning() {
        return false;
    }
}
