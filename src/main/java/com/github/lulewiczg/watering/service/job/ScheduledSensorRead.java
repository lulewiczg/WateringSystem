package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.WaterLevelReadAction;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Job for reading sensors.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.sensorsRead.enabled")
public class ScheduledSensorRead extends ScheduledIoJob {

    private final AppState state;

    private final WaterLevelReadAction readAction;

    private final JobRunner jobRunner;

    private final ActionRunner actionRunner;

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.sensorsRead.cron}")
    void schedule() {
        schedule(jobRunner);
    }

    @Override
    public String getName() {
        return "Sensors read";
    }

    @Override
    public void doJob(JobDto job) {
        log.debug("Reading sensors...");
        state.getTanks().forEach(i -> {
            ActionResultDto<Double> result = runNested(actionRunner, job, readAction, i.getSensor());
            log.debug("Read water level for {}: {}", i.getId(), result);
            i.getSensor().setLevel(result.getResult().intValue());
        });
    }

}
