package com.github.lulewiczg.watering.state.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pi4j.io.gpio.Pin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Sensor state.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {

    private String id;

    private Integer minLevel;

    private Integer maxLevel;

    private Integer level;

    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Pin pin;

}
