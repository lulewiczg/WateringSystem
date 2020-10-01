package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.AppState;
import com.github.lulewiczg.watering.service.io.IOService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Action for emergency stopping system
 */
@Component
@RequiredArgsConstructor
public class EmergencyStopAction implements Action<Void, Void> {

    private final AppState state;

    private final IOService service;

    @Override
    public Void doAction(Void param) {
        state.getTanks().forEach(i -> service.toggleOff(i.getValve().getConfig().getPin()));
        state.getOutputValves().forEach(i -> service.toggleOff(i.getConfig().getPin()));
        return null;
    }
}
