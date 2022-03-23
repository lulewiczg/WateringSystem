package com.github.lulewiczg.watering.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pi4j.io.gpio.Pin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Pump configuration.
 */
@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class PumpConfig implements PinnableConfig {

    @NotEmpty
    private String id;

    private String name;

    @NotEmpty
    private String pinName;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Pin pin;

    public PumpConfig(String id, String name, String pinName) {
        this.id = id;
        this.name = name;
        this.pinName = pinName;
    }
}
