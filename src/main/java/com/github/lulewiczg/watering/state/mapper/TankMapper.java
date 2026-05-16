package com.github.lulewiczg.watering.state.mapper;

import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.state.dto.Tank;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper for tank.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TankMapper {

    Tank map(TankConfig config);

    List<Tank> map(List<TankConfig> config);

}
