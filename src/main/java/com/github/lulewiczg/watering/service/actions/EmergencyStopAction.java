package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
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
public class EmergencyStopAction extends Action<Void, Void> {

    private final TanksCloseAction tanksCloseAction;

    private final TapsCloseAction tapsCloseAction;

    private final OutputsCloseAction outputsCloseAction;

    private final ActionRunner actionRunner;

    @Override
    protected Void doAction(ActionDto actionDto, Void param) {
        log.info("System emergency stop...");
        String nestedId = getNestedId(actionDto);
        ActionResultDto<Void> result = actionRunner.run(nestedId, tanksCloseAction, null);
        ActionResultDto<Void> result2 = actionRunner.run(nestedId, tapsCloseAction, null);
        ActionResultDto<Void> result3 = actionRunner.run(nestedId, outputsCloseAction, null);

        handleResult(result);
        handleResult(result2);
        handleResult(result3);

        return null;
    }

    @Override
    public String getDescription() {
        return "Closes all valves";
    }
}
