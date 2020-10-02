package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.service.actions.TanksCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Tank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.fill.enabled")
public class ScheduledOverflowWaterControl {

    private final TanksCloseAction tanksCloseAction;

    private final ValveOpenAction valveOpenAction;

    private final AppState state;

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.fill.cron}")
    void water() {
        log.info("Staring overflow control job...");
        if (state.getState() == SystemStatus.DRAINING) {
            log.info("Already draining");
            return;
        }
        List<Tank> tanks = state.getTanks().stream()
                .filter(i -> i.getSensor().getLevel() > i.getSensor().getMaxLevel()).collect(Collectors.toList());
        if (tanks.isEmpty()) {
            log.info("Water levels are OK");
            tanksCloseAction.doAction(null);
            state.setState(SystemStatus.IDLE);
            return;
        }
        state.setState(SystemStatus.DRAINING);
        log.info("Water level too high for {}", tanks);
        tanks.forEach(i -> valveOpenAction.doAction(i.getValve()));
        log.info("Draining tanks started.");
    }
}
