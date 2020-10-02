package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.dto.TankType;
import com.github.lulewiczg.watering.service.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Action for opening taps.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class TapsOpenAction implements Action<Void, Void> {

    private final AppState state;

    private final ValveOpenAction openAction;

    @Override
    public Void doAction(Void param) {
        log.info("Opening taps...");
        state.getTanks().stream().filter(i -> i.getConfig().getType() == TankType.UNLIMITED).forEach(i -> openAction.doAction(i.getValve()));
        return null;
    }
}
