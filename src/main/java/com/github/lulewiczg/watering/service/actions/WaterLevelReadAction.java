package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.Sensor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Action reading water level.
 */
@Component
@RequiredArgsConstructor
public class WaterLevelReadAction implements Action<Sensor, Double> {

    private final IOService service;

    @Override
    public Double doAction(Sensor sensor) {
        return service.analogRead(sensor.getConfig().getPin());
    }
}
