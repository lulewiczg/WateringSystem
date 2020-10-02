package com.github.lulewiczg.watering.state.mapper;

import com.github.lulewiczg.watering.config.dto.WaterLevelSensorConfig;
import com.github.lulewiczg.watering.state.dto.Sensor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for sensor.
 */
@Mapper(componentModel = "spring")
public interface SensorMapper {

    @Mapping(target = "level", ignore = true)
    Sensor map(WaterLevelSensorConfig sensor);

}
