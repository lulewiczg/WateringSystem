package com.github.lulewiczg.watering.state.mapper;

import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.state.dto.WaterSource;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for water source.
 */
@Mapper(componentModel = "spring")
public interface WaterSourceMapper {

    WaterSource map(TankConfig valve);

    List<WaterSource> map(List<TankConfig> valve);

}
