package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.*;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Job for scheduled watering.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.watering.enabled")
public class ScheduledWatering extends ScheduledJob {

    @Value("${com.github.lulewiczg.watering.schedule.watering.duration}")
    private Long wateringLength;

    private final TanksOpenAction tanksOpenAction;

    private final TanksCloseAction tanksCloseAction;

    private final OutputsOpenAction outputsOpenAction;

    private final OutputsCloseAction outputsCloseAction;

    private final AppState state;

    private final JobRunner jobRunner;

    private final ActionRunner actionRunner;

    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.watering.cron}")
    void schedule() {
        schedule(jobRunner);
    }

    @Override
    public String getName() {
        return "Watering";
    }

    @Override
    protected SystemStatus getJobStatus() {
        return SystemStatus.WATERING;
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
    public void doJob(JobDto job) {
        state.setState(SystemStatus.WATERING);
        ActionResultDto<Void> result = actionRunner.run(getNestedId(job), tanksOpenAction, null);
        handleResult(result);
        ActionResultDto<Void> result2 = actionRunner.run(getNestedId(job), outputsOpenAction, null);
        handleResult(result2);
        log.info("Valves opened");
        exec.schedule(() -> finish(job), wateringLength, TimeUnit.SECONDS);
    }

    private void finish(JobDto job) {
        log.info("Stopping watering job...");
        ActionResultDto<Void> result = actionRunner.run(getNestedId(job), tanksCloseAction, null);
        handleResult(result);
        ActionResultDto<Void> result2 = actionRunner.run(getNestedId(job), outputsCloseAction, null);
        handleResult(result2);
        state.setState(SystemStatus.IDLE);
        log.info("Watering finished!");
    }
}
