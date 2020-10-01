package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.dto.TankType;
import com.github.lulewiczg.watering.service.AppState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Action for closing taps.
 */
@Component
@RequiredArgsConstructor
public class TapsCloseAction implements Action<Void, Void> {

    private final AppState state;

    private final ValveCloseAction closeAction;

    @Override
    public Void doAction(Void param) {
        state.getTanks().stream().filter(i -> i.getConfig().getType() == TankType.UNLIMITED).forEach(i -> closeAction.doAction(i.getValve()));
        return null;
    }
}
