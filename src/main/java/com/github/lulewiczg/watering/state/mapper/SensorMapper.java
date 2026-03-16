package com.github.lulewiczg.watering.state.mapper;

import com.github.lulewiczg.watering.config.dto.WaterLevelSensorConfig;
import com.github.lulewiczg.watering.state.dto.Sensor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper for sensor.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SensorMapper {

    Sensor map(WaterLevelSensorConfig sensor);

}
