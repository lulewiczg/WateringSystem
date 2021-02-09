package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.service.actions.EmergencyStopAction;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Tank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Job for scheduled watering.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.escapeControl.enabled")
@ConditionalOnExpression("'${com.github.lulewiczg.watering.role}' != 'master'")
public class ScheduledWaterEscapeControl extends ScheduledJob {

    private final EmergencyStopAction emergencyStopAction;

    private final AppState state;

    private Map<String, Integer> prevLevels = new HashMap<>();

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.escapeControl.cron}")
    void schedule() {
        run();
    }

    @Override
    public String getName() {
        return "Water leak control";
    }

    @Override
    protected SystemStatus getJobStatus() {
        return SystemStatus.ERROR;
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
    protected void onStartFailed() {
        prevLevels = new HashMap<>();
    }

    @Override
    protected void doJob() {
        log.debug("Staring escape control job...");

        Map<String, Integer> levels = getLevels();
        List<Integer> leaks = levels.entrySet().stream().filter(i -> filter(i.getKey(), i.getValue()))
                .map(Map.Entry::getValue).collect(Collectors.toList());
        if (!leaks.isEmpty()) {
            log.error("Water leak in tanks: {}", leaks);
            state.setState(SystemStatus.ERROR);
            emergencyStopAction.doAction(null);
        }
        log.debug("Escape control finished.");
        prevLevels = levels;
    }

    private boolean filter(String id, Integer level) {
        Integer prevLevel = prevLevels.get(id);
        if (level == null || prevLevel == null) {
            return false;
        }
        return level < prevLevel;
    }

    private Map<String, Integer> getLevels() {
        return state.getTanks().stream().filter(i -> i.getSensor().getLevel() != null)
                .collect(Collectors.toMap(Tank::getId, i -> i.getSensor().getLevel()));
    }

}
