package com.github.lulewiczg.watering.state.mapper;

import com.github.lulewiczg.watering.config.dto.PumpConfig;
import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.state.dto.Pump;
import com.github.lulewiczg.watering.state.dto.Tank;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for pump.
 */
@Mapper(componentModel = "spring")
public interface PumpMapper {

    Pump map(PumpConfig config);

    List<Pump> map(List<PumpConfig> config);

}
