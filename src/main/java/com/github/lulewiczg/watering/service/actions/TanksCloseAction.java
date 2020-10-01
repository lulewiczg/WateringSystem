package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.dto.TankType;
import com.github.lulewiczg.watering.service.AppState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Action for closing tanks.
 */
@Component
@RequiredArgsConstructor
public class TanksCloseAction implements Action<Void, Void> {

    private final AppState state;

    private final ValveCloseAction closeAction;

    @Override
    public Void doAction(Void param) {
        state.getTanks().stream().filter(i -> i.getConfig().getType() == TankType.DEFAULT).forEach(i -> closeAction.doAction(i.getValve()));
        return null;
    }
}
