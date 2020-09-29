package com.github.lulewiczg.watering.config.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Water level sensor config.
 */
@Data
@Validated
@RequiredArgsConstructor
@AllArgsConstructor
public class WaterLevelSensor {

    @Valid
    @Min(0)
    @Max(100)
    private Integer minLevel;

    @Valid
    @Min(0)
    @Max(100)
    private Integer maxLevel;

    public void validate() {
        if (minLevel > maxLevel) {
            throw new IllegalStateException("Min water level can not be higher than max!");
        }
    }
}
