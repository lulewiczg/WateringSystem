package com.github.lulewiczg.watering.config.dto;

import com.pi4j.io.gpio.Pin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Water valve config.
 */
@Data
@Validated
@AllArgsConstructor
@RequiredArgsConstructor
public class ValveConfig implements Steerable {

    @Valid
    @NotNull
    private String name;

    @Valid
    @NotNull
    private ValveType type;

    @NotNull
    private String pinName;

    private boolean open;

    @EqualsAndHashCode.Exclude
    private Pin pin;

    public ValveConfig(@Valid @NotNull String name, @Valid @NotNull ValveType type, @NotNull String pinName, boolean open) {
        this.name = name;
        this.type = type;
        this.pinName = pinName;
        this.open = open;
    }
}
