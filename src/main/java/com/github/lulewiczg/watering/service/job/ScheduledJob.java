package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.Action;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.SystemStatus;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

/**
 * Abstract class for scheduled job.
 */
@Log4j2
public abstract class ScheduledJob {

    protected static final String SCHED = "sched.";

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
     * Schedules job.
     *
     * @param jobRunner job runner
     */
    protected void schedule(JobRunner jobRunner) {
        jobRunner.run(new JobDto(SCHED + getName(), SCHED + UUID.randomUUID().toString(), this));
    }

    /**
     * Action logic.
     *
     * @param job job DTO
     */
    protected abstract void doJob(JobDto job);

    /**
     * Action logic when already running.
     *
     * @param job job DTO
     */
    protected void doJobRunning(JobDto job) {
        //Do nothing
    }

    /**
     * Returns ID for nested call.
     *
     * @param job job
     * @return ID
     */
    protected String getNestedId(JobDto job) {
        return job.getId() + ".";
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

    public boolean isRunning() {
        return getJobStatus() == getState();
    }

    /**
     * Handles action result and throws exception if failed.
     *
     * @param result result
     */
    protected void handleResult(ActionResultDto<?> result) {
        if (result.getErrorMsg() != null) {
            throw new ActionException(result.getId(), result.getErrorMsg());
        }
    }

    /**
     * Runs nested action and checks result.
     *
     * @param runner       action runner
     * @param jobDto       job DTO
     * @param nestedAction nested action
     * @param param        nested action param
     * @param <T2>         nested action param type
     * @param <R2>         nested action return type
     * @return nesed action result
     */
    protected <T2, R2> ActionResultDto<R2> runNested(ActionRunner runner, JobDto jobDto, Action<T2, R2> nestedAction, T2 param) {
        ActionResultDto<R2> result = runner.run(getNestedId(jobDto), nestedAction, param);
        handleResult(result);
        return result;
    }

}
