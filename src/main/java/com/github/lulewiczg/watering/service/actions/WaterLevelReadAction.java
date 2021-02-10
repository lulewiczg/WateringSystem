package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Action for reading water level.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class WaterLevelReadAction implements Action<Sensor, Double> {

    private final IOService service;

    private final AppState state;

    @Override
    public String getParamDescription() {
        return "Sensor ID";
    }

    @Override
    public String getParamType() {
        return String.class.getSimpleName();
    }

    @Override
    public Double doAction(Sensor sensor) {
        log.info("Reading water level for sensor {}", sensor);
        return service.analogRead(sensor.getPin());
    }

    @Override
    public boolean isEnabled() {
        return state.getTanks().stream().anyMatch(i -> i.getSensor() != null);
    }
}
