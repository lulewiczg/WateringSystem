package com.github.lulewiczg.watering.state.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tank state.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tank {

    private Integer volume;

    private Sensor sensor;

}
