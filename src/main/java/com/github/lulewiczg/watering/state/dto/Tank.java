package com.github.lulewiczg.watering.state.dto;

import com.github.lulewiczg.watering.config.dto.TankType;
import lombok.*;

/**
 * Tank state.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tank {

    private Integer volume;

    private TankType type;

    private Sensor sensor;

    private Valve valve;

}
