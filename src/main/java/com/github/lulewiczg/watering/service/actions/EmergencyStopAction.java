package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Action for emergency stopping system
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class EmergencyStopAction implements Action<Void, Void> {

    private final TanksCloseAction tanksCloseAction;

    private final TapsCloseAction tapsCloseAction;

    private final OutputsCloseAction outputsCloseAction;

    @Override
    public Void doAction(Void param) {
        log.info("System emergency stop...");
        tanksCloseAction.doAction(null);
        tapsCloseAction.doAction(null);
        outputsCloseAction.doAction(null);
        return null;
    }

    @Override
    public String getDescription() {
        return "Closes all valves";
    }
}
