package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Action for opening taps.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class TapsOpenAction extends Action<Void, Void> {

    private final AppState state;

    private final ValveOpenAction openAction;

    private final ActionRunner actionRunner;

    @Override
    protected Void doAction(ActionDto actionDto, Void param) {
        log.info("Opening taps...");
        state.getTaps().forEach(i -> runNested(actionRunner, actionDto, openAction, i.getValve()));
        return null;
    }

    @Override
    public boolean isEnabled() {
        return !state.getTaps().isEmpty();
    }

    @Override
    public String getDescription() {
        return "Opens taps";
    }
}
