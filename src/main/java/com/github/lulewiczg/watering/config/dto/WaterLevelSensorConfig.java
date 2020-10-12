package com.github.lulewiczg.watering.config.dto;

import com.pi4j.io.gpio.Pin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * Water level sensor config.
 */
@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class WaterLevelSensorConfig implements Steerable {

    @NotEmpty
    private String id;

    @Valid
    @Min(0)
    @Max(100)
    private Integer minLevel;

    @Valid
    @Min(0)
    @Max(100)
    private Integer maxLevel;

    @NotEmpty
    private String pinName;

    @EqualsAndHashCode.Exclude
    private Pin pin;

    public void validate() {
        if (minLevel > maxLevel) {
            throw new IllegalStateException("Min water level can not be higher than max!");
        }
    }

    public WaterLevelSensorConfig(String id, Integer minLevel, Integer maxLevel, String pinName) {
        this.id = id;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.pinName = pinName;
    }
}
