package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.service.actions.EmergencyStopAction;
import com.github.lulewiczg.watering.service.actions.WaterLevelReadAction;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Job for scheduled watering.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.escapeControl.enabled")
public class ScheduledWaterEscapeControl {

    private final EmergencyStopAction emergencyStopAction;

    private final AppState state;

    private List<Integer> prevLevels;

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.escapeControl.cron}")
    void run() {
        log.info("Staring escape control job...");
        if (state.getState() == SystemStatus.WATERING || state.getState() == SystemStatus.DRAINING) {
            log.info("Water output in progress, stopping...");
            return;
        }
        if (prevLevels == null) {
            prevLevels = getLevels();
            return;
        }

        List<Integer> levels = getLevels();
        List<Integer> damagedTanks = IntStream.range(0, levels.size()).boxed()
                .filter(i -> levels.get(i) < prevLevels.get(i)).collect(Collectors.toList());
        if (!damagedTanks.isEmpty()) {
            log.info("Water leak in tanks: {}", damagedTanks);
            state.setState(SystemStatus.ERROR);
            emergencyStopAction.doAction(null);
        }
        log.info("Escape control finished.");
    }

    private List<Integer> getLevels() {
        return state.getTanks().stream().map(i -> i.getSensor().getLevel()).collect(Collectors.toList());
    }

}
