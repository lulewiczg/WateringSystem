package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Action for closing valve.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class ValveCloseAction implements Action<Valve, Void> {

    private final IOService service;

    @Override
    public String getParamDescription() {
        return "Valve ID";
    }

    @Override
    public String getParamType() {
        return String.class.getSimpleName();
    }

    @Override
    public Void doAction(Valve valve) {
        log.info("Closing valve: {}", valve.getName());
        service.toggleOff(valve.getPin());
        valve.setOpen(false);
        return null;
    }
}
