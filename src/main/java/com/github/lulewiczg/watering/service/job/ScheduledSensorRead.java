package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.service.actions.WaterLevelReadAction;
import com.github.lulewiczg.watering.state.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
@ConditionalOnExpression("'${com.github.lulewiczg.watering.role}' != 'master'")
public class ScheduledSensorRead extends ScheduledIoJob {

    private final AppState state;

    private final WaterLevelReadAction readAction;

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.sensorsRead.cron}")
    void schedule() {
        run();
    }

    @Override
    public String getName() {
        return "Sensors read";
    }

    @Override
    protected void doJob() {
        log.debug("Reading sensors...");
        state.getTanks().forEach(i -> {
            double result = readAction.doAction(i.getSensor());
            log.debug("Read water level for {}: {}", i.getId(), result);
            i.getSensor().setLevel((int) result);
        });
    }

}
