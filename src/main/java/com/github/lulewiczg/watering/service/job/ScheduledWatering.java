package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.WateringAction;
import com.github.lulewiczg.watering.service.actions.dto.WateringDto;
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

    private final WateringAction wateringAction;

    private final AppState state;

    private final JobRunner jobRunner;

    private final ActionRunner actionRunner;

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
        List<Valve> outputs = state.getOutputs().stream().filter(i -> i.getWateringTime() != null).collect(Collectors.toList());
        log.debug("Starting watering job for valves {}", outputs);
        outputs.forEach(i -> runNested(actionRunner, job, wateringAction, new WateringDto(i.getId(), i, i.getWateringTime(), null)));
    }

}
