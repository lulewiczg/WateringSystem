package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
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

    private final ActionRunner actionRunner;

    @Override
    protected Void doAction(ActionDto actionDto, Void param) {
        log.info("Opening outputs...");
        state.getOutputs().forEach(i -> {
            ActionResultDto<Void> result = actionRunner.run(getNestedId(actionDto), openAction, i);
            handleResult(result);
        });
        return null;
    }

    @Override
    public String getDescription() {
        return "Opens output valves";
    }
}
