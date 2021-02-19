package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.TanksCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
import com.github.lulewiczg.watering.service.dto.ActionDto;
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
public class ScheduledOverflowWaterControl extends ScheduledJob {

    private final TanksCloseAction tanksCloseAction;

    private final ValveOpenAction valveOpenAction;

    private final AppState state;

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.fill.cron}")
    void schedule() {
        run(new JobDto());
    }

    @Override
    public String getName() {
        return "Water overflow control";
    }

    @Override
    protected SystemStatus getJobStatus() {
        return SystemStatus.DRAINING;
    }

    @Override
    protected SystemStatus getState() {
        return state.getState();
    }

    @Override
    protected void doJob(JobDto job) {
        List<Tank> tanks = findOverflowTanks();
        if (tanks.isEmpty()) {
            log.debug("Water levels are OK, no need to drain");
            return;
        }
        state.setState(SystemStatus.DRAINING);
        log.info("Water level too high for {}", () -> tanks.stream().map(Tank::getId).collect(Collectors.toList()));
        tanks.forEach(i -> valveOpenAction.doAction(job.toAction(), i.getValve()));
        log.info("Draining tanks started.");
    }

    @Override
    protected void doJobRunning(JobDto job) {
        List<Tank> tanks = findOverflowTanks();
        if (tanks.isEmpty()) {
            log.info("Water levels are OK, stopping");
            tanksCloseAction.doAction(new ActionDto(), null);
            state.setState(SystemStatus.IDLE);
        }
    }

    private List<Tank> findOverflowTanks() {
        return state.getTanks().stream()
                .filter(i -> i.getSensor().getLevel() != null && i.getSensor().getLevel() > i.getSensor().getMaxLevel()).collect(Collectors.toList());
    }
}
