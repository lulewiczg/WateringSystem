package com.github.lulewiczg.watering.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pi4j.io.gpio.Pin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Water level sensor config.
 */
@Data
@Validated
@RequiredArgsConstructor
@AllArgsConstructor
public class WaterLevelSensorConfig implements Steerable {

    @Valid
    @Min(0)
    @Max(100)
    private Integer minLevel;

    @Valid
    @Min(0)
    @Max(100)
    private Integer maxLevel;

    @NotNull
    private String pinName;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Pin pin;

    public void validate() {
        if (minLevel > maxLevel) {
            throw new IllegalStateException("Min water level can not be higher than max!");
        }
    }

    public WaterLevelSensorConfig(@Valid @Min(0) @Max(100) Integer minLevel, @Valid @Min(0) @Max(100) Integer maxLevel, @NotNull String pinName) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.pinName = pinName;
    }
}
