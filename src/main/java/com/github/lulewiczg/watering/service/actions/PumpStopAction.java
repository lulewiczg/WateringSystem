package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Pump;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Action for stopping pump.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class PumpStopAction extends Action<Pump, Void> {

    private final IOService service;

    private final AppState state;

    @Override
    public String getParamDescription() {
        return "Pump ID";
    }

    @Override
    public Class<?> getParamType() {
        return String.class;
    }

    @Override
    public Class<?> getDestinationParamType() {
        return Pump.class;
    }

    @Override
    public boolean isEnabled() {
        return state.getPumps() != null && !state.getPumps().isEmpty();
    }

    @Override
    @Cacheable
    public List<?> getAllowedValues() {
        return state.getPumps().stream().map(Pump::getId).collect(Collectors.toList());
    }

    @Override
    protected Void doAction(ActionDto actionDto, Pump pump) {
        log.info("Stopping pump: {}", pump.getName());
        service.toggleOff(pump.getPin());
        pump.setRunning(false);
        return null;
    }

    @Override
    public String getDescription() {
        return "Stops given pump";
    }

}
