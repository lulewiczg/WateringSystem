package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.ValveCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    @Value("${com.github.lulewiczg.watering.schedule.setDefaults.enabled}")
    private boolean enabled;

    @PostConstruct
    void postConstruct() {
        if (enabled) {
            run(new JobDto());
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
    protected void doJob() {
        log.info("Settings defaults...");
        state.getTanks().stream().map(Tank::getValve).forEach(this::setValveState);
        state.getOutputs().forEach(this::setValveState);
    }

    private void setValveState(Valve i) {
        if (i.isOpen()) {
            openAction.doAction(i);
        } else {
            closeAction.doAction(i);
        }
    }

}
