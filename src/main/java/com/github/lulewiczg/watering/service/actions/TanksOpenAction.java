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
 * Action for opening tanks.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class TanksOpenAction extends Action<Void, Void> {

    private final AppState state;

    private final ValveOpenAction openAction;

    private final PumpStartAction pumpStartAction;

    private final ActionRunner actionRunner;

    @Override
    protected Void doAction(ActionDto actionDto, Void param) {
        log.info("Opening tanks...");
        state.getTanks().forEach(i -> {
            Optional.ofNullable(i.getValve()).ifPresent(j -> runNested(actionRunner, actionDto, openAction, j));
            Optional.ofNullable(i.getPump()).ifPresent(j -> runNested(actionRunner, actionDto, pumpStartAction, j));
        });
        return null;
    }

    @Override
    public String getDescription() {
        return "Opens tank valves";
    }
}
