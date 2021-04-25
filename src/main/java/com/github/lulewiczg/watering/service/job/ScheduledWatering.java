package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.*;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Job for scheduled watering.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.watering.enabled")
public class ScheduledWatering extends ScheduledJob {

    private final TanksOpenAction tanksOpenAction;

    private final TanksCloseAction tanksCloseAction;

    private final ValveOpenAction valveOpenAction;

    private final ValveCloseAction valveCloseAction;

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
        runNested(actionRunner, job, tanksOpenAction, null);
        List<Valve> outputs = state.getOutputs().stream().filter(i -> i.getWateringTime() != null).collect(Collectors.toList());
        AtomicInteger counter = new AtomicInteger(outputs.size());
        outputs.forEach(i -> {
            log.info("Opening valve {}", i.getId());
            runNested(actionRunner, job, valveOpenAction, i);
            exec.schedule(() -> finish(job, i, counter), i.getWateringTime(), TimeUnit.SECONDS);
        });
    }

    private void finish(JobDto job, Valve valve, AtomicInteger counter) {
        log.info("Closing valve {}", valve.getId());
        runNested(actionRunner, job, valveCloseAction, valve);
        int c = counter.decrementAndGet();
        if (c == 0) {
            log.info("Stopping watering job...");
            runNested(actionRunner, job, tanksCloseAction, null);
            state.setState(SystemStatus.IDLE);
            log.info("Watering finished!");
        }
    }
}
