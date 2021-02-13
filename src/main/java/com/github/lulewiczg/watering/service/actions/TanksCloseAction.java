package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Action for closing tanks.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class TanksCloseAction extends Action<Void, Void> {

    private final AppState state;

    private final ValveCloseAction closeAction;

    @Override
    protected Void doActionInternal(ActionDto actionDto, Void param) {
        log.info("Closing tanks...");
        state.getTanks().forEach(i -> closeAction.doAction(actionDto, i.getValve()));
        return null;
    }

    @Override
    public String getDescription() {
        return "Closes tanks valves";
    }
}
