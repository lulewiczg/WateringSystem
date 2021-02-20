package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Action for opening valve.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class ValveOpenAction extends Action<Valve, Void> {

    private final IOService service;

    private final AppState state;

    @Override
    public String getParamDescription() {
        return "Valve ID";
    }

    @Override
    public Class<?> getParamType() {
        return String.class;
    }

    @Override
    public Class<?> getDestinationParamType() {
        return Valve.class;
    }

    @Override
    @Cacheable
    public List<?> getAllowedValues() {
        return state.getAllValves().stream().map(Valve::getId).collect(Collectors.toList());
    }

    @Override
    protected Void doAction(ActionDto actionDto, Valve valve) {
        log.info("Opening valve: {}", valve.getName());
        service.toggleOn(valve.getPin());
        valve.setOpen(true);
        return null;
    }

    @Override
    public String getDescription() {
        return "Opens given valve";
    }
}
