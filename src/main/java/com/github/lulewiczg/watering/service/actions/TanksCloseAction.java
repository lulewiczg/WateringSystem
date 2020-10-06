package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.state.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Action for closing tanks.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class TanksCloseAction implements Action<Void, Void> {

    private final AppState state;

    private final ValveCloseAction closeAction;

    @Override
    public Void doAction(Void param) {
        log.info("Closing tanks...");
        state.getTanks().forEach(i -> closeAction.doAction(i.getValve()));
        return null;
    }
}
