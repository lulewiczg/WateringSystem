package com.github.lulewiczg.watering.state.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pi4j.io.gpio.Pin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sensor state.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {

    private Integer minLevel;

    private Integer maxLevel;

    private int level;

    @JsonIgnore
    private Pin pin;

}
