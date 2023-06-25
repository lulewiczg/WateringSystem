package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.EmergencyStopAction;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Tank;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
@ConditionalOnMissingBean(MasterConfig.class)
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.escapeControl.enabled")
public class ScheduledWaterEscapeControl extends ScheduledJob {

    private final EmergencyStopAction emergencyStopAction;
    private final AppState state;
    private final JobRunner jobRunner;
    private final ActionRunner actionRunner;
    private final int levelDiff;

    private Map<String, Integer> prevLevels = new HashMap<>();

    public ScheduledWaterEscapeControl(EmergencyStopAction emergencyStopAction, AppState state, JobRunner jobRunner, ActionRunner actionRunner,
                                       @Value("${com.github.lulewiczg.job.waterEscape.levelDiff:3}") int levelDiff) {
        this.emergencyStopAction = emergencyStopAction;
        this.state = state;
        this.jobRunner = jobRunner;
        this.actionRunner = actionRunner;
        this.levelDiff = levelDiff;
    }

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.escapeControl.cron}")
    void schedule() {
        schedule(jobRunner);
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
    public boolean isRunning() {
        return false;
    }

    @Override
    protected void onStartFailed() {
        prevLevels = new HashMap<>();
    }

    @Override
    public void doJob(JobDto job) {
        log.debug("Staring escape control job...");

        Map<String, Integer> levels = getLevels();
        List<Integer> leaks = levels.entrySet().stream().filter(i -> filter(i.getKey(), i.getValue()))
                .map(Map.Entry::getValue).collect(Collectors.toList());
        if (!leaks.isEmpty()) {
            log.error("Water leak in tanks: {}", leaks);
            state.setState(SystemStatus.ERROR);
            runNested(actionRunner, job, emergencyStopAction, null);
        }
        log.debug("Escape control finished.");
        prevLevels = levels;
    }

    private boolean filter(String id, Integer level) {
        Integer prevLevel = prevLevels.get(id);
        if (level == null || prevLevel == null) {
            return false;
        }
        return level < prevLevel - levelDiff;
    }

    private Map<String, Integer> getLevels() {
        return state.getTanks().stream().filter(i -> i.getSensor() != null && i.getSensor().getLevel() != null)
                .collect(Collectors.toMap(Tank::getId, i -> i.getSensor().getLevel()));
    }

}
