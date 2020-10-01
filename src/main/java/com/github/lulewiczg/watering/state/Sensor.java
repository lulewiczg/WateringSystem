package com.github.lulewiczg.watering.state;

import com.github.lulewiczg.watering.config.dto.WaterLevelSensorConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Sensor state.
 */
@Data
@RequiredArgsConstructor
public class Sensor {

    private final WaterLevelSensorConfig config;

    private int level;

}
