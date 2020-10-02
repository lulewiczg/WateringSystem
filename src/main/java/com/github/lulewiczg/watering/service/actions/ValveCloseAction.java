package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Action for closing valve.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class ValveCloseAction implements Action<Valve, Void> {

    private final IOService service;

    @Override
    public Void doAction(Valve valve) {
        log.info("Closing valve: {}", valve.getName());
        service.toggleOff(valve.getPin());
        return null;
    }
}
