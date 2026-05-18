package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.TemperatureReadAction;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Job for scheduled watering.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.temperatureRead.enabled")
public class ScheduledTemperatureRead extends ScheduledJob {

    private final AppState state;

    private final JobRunner jobRunner;

    private final ActionRunner actionRunner;

    private final TemperatureReadAction temperatureReadAction;

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.temperatureRead.cron}")
    void schedule() {
        schedule(jobRunner);
    }

    @Override
    public String getName() {
        return "Temperature read";
    }

    @Override
    protected SystemStatus getState() {
        return state.getState();
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public boolean canBeStarted() {
        return true;
    }

    @SneakyThrows
    @Override
    public void doJob(JobDto job) {
        log.debug("Running scheduled temperature check...");
        runNested(actionRunner, job, temperatureReadAction, null);
    }

}
