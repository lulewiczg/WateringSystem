package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Action for closing all outputs.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class OutputsCloseAction implements Action<Void, Void> {

    private final AppState state;

    private final ValveCloseAction closeAction;

    @Override
    public Void doAction(Void param) {
        log.info("Closing outputs...");
        state.getOutputValves().forEach(closeAction::doAction);
        return null;
    }
}
