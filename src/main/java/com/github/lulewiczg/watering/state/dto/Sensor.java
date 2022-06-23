package com.github.lulewiczg.watering.state.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
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

    private Integer level;

    private Integer minLevel;

    private Integer maxLevel;

    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Address address;

    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Pin powerControlPin;

    private int resistorsNumber;

    private int passiveResistance;

    private int stepResistance;

    private double voltage;

}
