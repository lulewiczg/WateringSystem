package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.dto.WateringDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Action for watering.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class WateringAction extends Action<WateringDto, Void> {

    private final IOService service;

    private final AppState state;

    private final ValveOpenAction openAction;

    private final ValveCloseAction closeAction;

    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

    private final ActionRunner actionRunner;

    private final CountDownLatch latch = new CountDownLatch(1);

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
        state.setState(SystemStatus.WATERING);
        log.info("Running watering: {}", wateringDto);
        wateringDto.getData().forEach(i -> {
            runNested(actionRunner, actionDto, openAction, i.getValve());
            exec.schedule(() -> close(actionDto, i.getValve()), i.getSeconds(), TimeUnit.SECONDS);

        });
//        state.setState(SystemStatus.IDLE);

        return null;
    }

    private void close(ActionDto actionDto, Valve valve) {
        log.info("Closing valve {}...", valve.getId());
        runNested(actionRunner, actionDto, closeAction, valve);
    }

    @Override
    public String getDescription() {
        return "Opens given valves";
    }

}
