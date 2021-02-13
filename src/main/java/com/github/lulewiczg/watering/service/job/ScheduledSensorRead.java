package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.WaterLevelReadAction;
import com.github.lulewiczg.watering.service.dto.ActionDto;
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

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.sensorsRead.cron}")
    void schedule() {
        run(new JobDto());
    }

    @Override
    public String getName() {
        return "Sensors read";
    }

    @Override
    protected void doJob() {
        log.debug("Reading sensors...");
        state.getTanks().forEach(i -> {
            ActionResultDto<Double> result = readAction.doAction(new ActionDto(), i.getSensor());
            log.debug("Read water level for {}: {}", i.getId(), result);
            i.getSensor().setLevel(result.getResult().intValue());
        });
    }

}
