package com.github.lulewiczg.watering.state.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
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
    private Integer powerControlPin;

    private int minResistance;

    private int maxResistance;

    private double voltage;

}
