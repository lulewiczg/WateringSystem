package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.service.actions.OutputsCloseAction;
import com.github.lulewiczg.watering.service.actions.OutputsOpenAction;
import com.github.lulewiczg.watering.service.actions.TanksCloseAction;
import com.github.lulewiczg.watering.service.actions.TanksOpenAction;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.watering.enabled")
@ConditionalOnExpression("'${com.github.lulewiczg.watering.role}' != 'master'")
public class ScheduledWatering extends ScheduledJob {

    @Value("${com.github.lulewiczg.watering.schedule.watering.duration}")
    private Long wateringLength;

    private final TanksOpenAction tanksOpenAction;

    private final TanksCloseAction tanksCloseAction;

    private final OutputsOpenAction outputsOpenAction;

    private final OutputsCloseAction outputsCloseAction;

    private final AppState state;

    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.watering.cron}")
    void schedule() {
        run();
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
    protected boolean isRunning() {
        return false;
    }

    @Override
    protected void doJob() {
        state.setState(SystemStatus.WATERING);
        tanksOpenAction.doAction(null);
        outputsOpenAction.doAction(null);
        log.info("Valves opened");
        exec.schedule(this::finish, wateringLength, TimeUnit.SECONDS);
    }

    private void finish() {
        log.info("Stopping watering job...");
        tanksCloseAction.doAction(null);
        outputsCloseAction.doAction(null);
        state.setState(SystemStatus.IDLE);
        log.info("Watering finished!");
    }
}
