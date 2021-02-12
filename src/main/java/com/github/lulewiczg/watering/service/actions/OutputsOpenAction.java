package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.state.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Action for opening all outputs.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class OutputsOpenAction extends Action<Void, Void> {

    private final AppState state;

    private final ValveOpenAction openAction;

    @Override
    public Void doAction(Void param) {
        log.info("Opening outputs...");
        state.getOutputs().forEach(openAction::doAction);
        return null;
    }

    @Override
    public String getDescription() {
        return "Opens output valves";
    }
}
