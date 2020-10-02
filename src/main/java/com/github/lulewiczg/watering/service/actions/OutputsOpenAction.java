package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Action for opening all outputs.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class OutputsOpenAction implements Action<Void, Void> {

    private final AppState state;

    private final ValveOpenAction openAction;

    @Override
    public Void doAction(Void param) {
        log.info("Opening outputs...");
        state.getOutputValves().forEach(openAction::doAction);
        return null;
    }
}