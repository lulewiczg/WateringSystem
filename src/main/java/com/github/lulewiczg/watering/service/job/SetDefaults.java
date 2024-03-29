package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.PumpStopAction;
import com.github.lulewiczg.watering.service.actions.ValveCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * Job for settings defaults on startup.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class SetDefaults extends ScheduledJob {

    private final AppState state;

    private final ValveOpenAction openAction;

    private final ValveCloseAction closeAction;

    private final PumpStopAction pumpStopAction;

    private final JobRunner jobRunner;

    private final ActionRunner actionRunner;

    private final IOService ioService;

    @Value("${com.github.lulewiczg.watering.schedule.setDefaults.enabled}")
    private boolean enabled;

    @PostConstruct
    void postConstruct() {
        if (enabled) {
            schedule(jobRunner);
        }
    }

    @Override
    public String getName() {
        return "Defaults setting";
    }

    @Override
    public boolean canBeStarted() {
        return true;
    }

    @Override
    public void doJob(JobDto job) {
        log.info("Settings defaults...");
        state.getTanks().stream().map(Tank::getValve).filter(Objects::nonNull).forEach(i -> setValveState(i, job));
        state.getOutputs().forEach(i -> setValveState(i, job));
        state.getTaps().forEach(i -> setValveState(i.getValve(), job));
        state.getTanks().stream().map(Tank::getSensor).filter(Objects::nonNull).
                map(Sensor::getPowerControlPin).distinct().forEach(ioService::toggleOff);
        state.getPumps().forEach(i -> runNested(actionRunner, job, pumpStopAction, i));
    }

    private void setValveState(Valve i, JobDto jobDto) {
        if (i.isOpen()) {
            runNested(actionRunner, jobDto, openAction, i);
        } else {
            runNested(actionRunner, jobDto, closeAction, i);
        }
    }

}
