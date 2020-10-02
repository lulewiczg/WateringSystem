package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.service.actions.*;
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
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.overflow.enabled")
public class ScheduledWaterFillControl {

    private final TanksCloseAction tanksCloseAction;

    private final TapsOpenAction tapsOpenAction;

    private final ValveOpenAction valveOpenAction;

    private final OutputsCloseAction outputsCloseAction;

    private final AppState state;

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.overflow.cron}")
    void water() {
        log.info("Staring flow level control job...");
        if (state.getState() == SystemStatus.FILLING) {
            log.info("Already filling");
        }
        state.setState(SystemStatus.FILLING);
        List<Tank> tanks = state.getTanks().stream().filter(i -> i.getSensor().getLevel() < i.getSensor().getMinLevel())
                .collect(Collectors.toList());
        if (tanks.isEmpty()) {
            log.info("Water levels are OK");
            tanksCloseAction.doAction(null);
            state.setState(SystemStatus.IDLE);
            return;
        }
        log.info("Water level too low for {}", tanks);
        outputsCloseAction.doAction(null);
        tapsOpenAction.doAction(null);
        tanks.forEach(i -> valveOpenAction.doAction(i.getValve()));
        log.info("Filling tanks started.");
    }
}
