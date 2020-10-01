package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.Valve;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Action for opening valve.
 */
@Component
@RequiredArgsConstructor
public class ValveOpenAction implements Action<Valve, Void> {

    private final IOService service;

    @Override
    public Void doAction(Valve valve) {
        service.toggleOn(valve.getConfig().getPin());
        return null;
    }
}
