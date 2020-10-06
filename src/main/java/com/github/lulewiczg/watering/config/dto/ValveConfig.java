package com.github.lulewiczg.watering.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pi4j.io.gpio.Pin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Water valve config.
 */
@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class ValveConfig implements Steerable {

    @NotNull
    private String id;

    @Valid
    @NotNull
    private String name;

    @Valid
    @NotNull
    private ValveType type;

    @NotNull
    private String pinName;

    private boolean open;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Pin pin;

    public ValveConfig(String id, String name, ValveType type, String pinName, boolean open) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.pinName = pinName;
        this.open = open;
    }
}
