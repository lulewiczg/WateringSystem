package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.AppState;
import com.github.lulewiczg.watering.service.io.IOService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Action for emergency stopping system
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class EmergencyStopAction implements Action<Void, Void> {

    private final AppState state;

    private final IOService service;

    @Override
    public Void doAction(Void param) {
        log.info("System emergency stop...");
        state.getTanks().forEach(i -> service.toggleOff(i.getValve().getConfig().getPin()));
        state.getOutputValves().forEach(i -> service.toggleOff(i.getConfig().getPin()));
        return null;
    }
}
