package com.github.lulewiczg.watering.state.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pi4j.io.gpio.Pin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Pump state.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pump {

    private String id;

    private String name;

    boolean running;

    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Pin pin;

}
