package com.github.lulewiczg.watering.state.mapper;

import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.state.dto.WaterSource;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper for water source.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WaterSourceMapper {

    WaterSource map(TankConfig valve);

    List<WaterSource> map(List<TankConfig> valve);

}
