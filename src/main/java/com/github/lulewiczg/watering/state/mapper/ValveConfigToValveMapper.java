package com.github.lulewiczg.watering.state.mapper;

import com.github.lulewiczg.watering.config.dto.ValveConfig;
import com.github.lulewiczg.watering.state.dto.Valve;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for valve.
 */
@Mapper(componentModel = "spring")
public interface ValveConfigToValveMapper {

    Valve map(ValveConfig valve);

    List<Valve> map(List<ValveConfig> valve);

}
