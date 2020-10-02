package com.github.lulewiczg.watering.state.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.pi4j.io.gpio.Pin;
import lombok.*;

/**
 * Valve status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Valve {

    private String name;

    private ValveType type;

    private boolean open;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Pin pin;

}
