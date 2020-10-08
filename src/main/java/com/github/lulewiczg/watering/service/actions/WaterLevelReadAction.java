package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Action for reading water level.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class WaterLevelReadAction implements Action<Sensor, Double> {

    private final IOService service;

    private final AppState state;

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
