package com.github.lulewiczg.watering.state.mapper;

import com.github.lulewiczg.watering.config.dto.PumpConfig;
import com.github.lulewiczg.watering.state.dto.Pump;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper for pump.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PumpMapper {

    Pump map(PumpConfig config);

    List<Pump> map(List<PumpConfig> config);

}
