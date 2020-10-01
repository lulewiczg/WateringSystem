package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.Valve;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Action for closing valve.
 */
@Component
@RequiredArgsConstructor
public class ValveCloseAction implements Action<Valve, Void> {

    private final IOService service;

    @Override
    public Void doAction(Valve valve) {
        service.toggleOff(valve.getConfig().getPin());
        return null;
    }
}
