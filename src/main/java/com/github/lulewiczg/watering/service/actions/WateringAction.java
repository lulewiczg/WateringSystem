package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.dto.WateringDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Action for watering.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class WateringAction extends Action<WateringDto, Void> {

    private final AppState state;

    private final ValveOpenAction openAction;

    private final ValveCloseAction closeAction;

    private final TanksCloseAction tanksCloseAction;

    private final TanksOpenAction tanksOpenAction;

    private final ActionRunner actionRunner;

    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

    @Override
    public String getParamDescription() {
        return "Valves data";
    }

    @Override
    public Class<?> getParamType() {
        return WateringDto.class;
    }

    @Override
    public Class<?> getDestinationParamType() {
        return WateringDto.class;
    }

    @Override
    protected Void doAction(ActionDto actionDto, WateringDto wateringDto) {
        log.info("Running watering: {}", wateringDto);
        state.setState(SystemStatus.WATERING);
        runNested(actionRunner, actionDto, tanksOpenAction, null);
        runNested(actionRunner, actionDto, openAction, wateringDto.getValve());
        exec.schedule(() -> finish(actionDto, wateringDto), wateringDto.getSeconds(), TimeUnit.SECONDS);

        return null;
    }

    private void finish(ActionDto actionDto, WateringDto wateringDto) {
        log.info("Closing valve {}...", wateringDto.getValve().getId());
        runNested(actionRunner, actionDto, closeAction, wateringDto.getValve());
        int c = wateringDto.getCounter().decrementAndGet();
        if (c == 0) {
            log.info("Stopping watering job...");
            runNested(actionRunner, actionDto, tanksCloseAction, null);
            state.setState(SystemStatus.IDLE);
            log.info("Watering finished!");
        }
    }

    @Override
    public String getDescription() {
        return "Opens given valves";
    }

}
