package com.github.lulewiczg.watering.state.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.pi4j.io.gpio.Pin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Valve status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Valve {

    private String id;

    private String name;

    private ValveType type;

    private boolean open;

    private boolean overflowOutput;

    private Integer wateringTime;

    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Pin pin;

}
