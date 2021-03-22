package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Action for reading water level.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class WaterLevelReadAction extends Action<Sensor, Double> {

    private final IOService service;

    private final AppState state;

    @Override
    public String getParamDescription() {
        return "Sensor ID";
    }

    @Override
    public Class<?> getParamType() {
        return String.class;
    }

    @Override
    public Class<?> getDestinationParamType() {
        return Sensor.class;
    }

    @Override
    @Cacheable
    public List<?> getAllowedValues() {
        return state.getTanks().stream().map(i -> i.getSensor().getId()).collect(Collectors.toList());
    }

    @Override
    protected Double doAction(ActionDto actionDto, Sensor sensor) {
        log.info("Reading water level for sensor {}", sensor);
        return service.analogRead(sensor.getAddress(), sensor.getPowerControlPin());
    }

    @Override
    public boolean isEnabled() {
        return state.getTanks().stream().anyMatch(i -> i.getSensor() != null);
    }

    @Override
    public String getDescription() {
        return "Reads water level from sensor";
    }

    @Override
    public Class<?> getReturnType() {
        return Double.class;
    }
}
