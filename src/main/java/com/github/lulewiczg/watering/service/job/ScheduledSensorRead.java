package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.service.actions.WaterLevelReadAction;
import com.github.lulewiczg.watering.state.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Job for reading sensors.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.sensorsRead.enabled")
public class ScheduledSensorRead {

    private final AppState state;

    private final WaterLevelReadAction readAction;

    /**
     * Runs action.
     */
    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.sensorsRead.cron}")
    public void run() {
        log.info("Reading sensors...");
        state.getTanks().forEach(i -> {
            double result = readAction.doAction(i.getSensor());
            log.info("Read water level for {}: {}", i.getId(), result);
            i.getSensor().setLevel((int) result);
        });
    }

}
