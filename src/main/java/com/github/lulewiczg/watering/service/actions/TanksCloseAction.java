package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    private final PumpStopAction pumpStopAction;

    private final ActionRunner actionRunner;

    @Override
    protected Void doAction(ActionDto actionDto, Void param) {
        log.info("Closing tanks...");
        state.getTanks().forEach(i -> {
            Optional.ofNullable(i.getPump()).ifPresent(j -> runNested(actionRunner, actionDto, pumpStopAction, j));
            Optional.ofNullable(i.getValve()).ifPresent(j -> runNested(actionRunner, actionDto, closeAction, j));
        });
        return null;
    }

    @Override
    public String getDescription() {
        return "Closes tanks valves";
    }
}
