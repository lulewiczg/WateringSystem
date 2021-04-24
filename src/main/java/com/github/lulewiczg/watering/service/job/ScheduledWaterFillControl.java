package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.*;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Tank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Job for scheduled water tanks filling.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.fill.enabled")
public class ScheduledWaterFillControl extends ScheduledJob {

    private final TanksCloseAction tanksCloseAction;

    private final TapsOpenAction tapsOpenAction;

    private final TapsCloseAction tapsCloseAction;

    private final ValveOpenAction valveOpenAction;

    private final OutputsCloseAction outputsCloseAction;

    private final AppState state;

    private final JobRunner jobRunner;

    private final ActionRunner actionRunner;

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.fill.cron}")
    void schedule() {
        schedule(jobRunner);
    }

    @Override
    public String getName() {
        return "Water fill";
    }

    @Override
    protected SystemStatus getJobStatus() {
        return SystemStatus.FILLING;
    }

    @Override
    protected SystemStatus getState() {
        return state.getState();
    }

    @Override
    public void doJob(JobDto job) {
        List<Tank> tanks = findTanks();
        if (tanks.isEmpty()) {
            log.debug("Water levels are OK");
            return;
        }
        log.info("Water level too low for {}", tanks);
        state.setState(SystemStatus.FILLING);
        runNested(actionRunner, job, outputsCloseAction, null);
        runNested(actionRunner, job, tapsOpenAction, null);
        tanks.forEach(i -> runNested(actionRunner, job, valveOpenAction, i.getValve()));
        log.info("Filling tanks started.");
    }

    @Override
    public void doJobRunning(JobDto job) {
        List<Tank> tanks = findTanks();
        if (tanks.isEmpty()) {
            log.info("Water levels are OK, filling finished");
            runNested(actionRunner, job, tanksCloseAction, null);
            runNested(actionRunner, job, tapsCloseAction, null);
            state.setState(SystemStatus.IDLE);
        }
    }

    private List<Tank> findTanks() {
        return state.getTanks().stream().filter(i -> i.getSensor() != null && i.getSensor().getLevel() != null
                && i.getSensor().getLevel() < i.getSensor().getMinLevel())
                .collect(Collectors.toList());
    }
}
