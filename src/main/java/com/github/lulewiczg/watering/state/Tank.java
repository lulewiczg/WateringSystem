package com.github.lulewiczg.watering.state;

import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.config.dto.TankType;
import lombok.Data;

/**
 * Tank state.
 */
@Data
public class Tank {

    private final TankConfig config;

    private final Valve valve;

    private final Sensor sensor;

    public Tank(TankConfig config) {
        this.config = config;
        this.valve = new Valve(config.getValve());
        this.sensor = new Sensor(config.getSensor());
    }

    public int getRemaining() {
        if (config.getType() == TankType.DEFAULT) {
            return config.getVolume() * sensor.getLevel();
        }
        return -1;
    }
}
