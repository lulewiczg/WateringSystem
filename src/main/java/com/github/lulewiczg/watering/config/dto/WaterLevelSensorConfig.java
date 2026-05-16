package com.github.lulewiczg.watering.config.dto;

import com.github.lulewiczg.watering.service.ina219.enums.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

/**
 * Water level sensor config.
 */
@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class WaterLevelSensorConfig {

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

    @NotNull
    @EqualsAndHashCode.Exclude
    private Address address;

    private Integer powerControlPin;

    @Min(1)
    private int minResistance;

    @Min(1)
    private int maxResistance;

    @Min(1)
    private double voltage;

    public void validate() {
        if (minLevel > maxLevel) {
            throw new IllegalStateException("Min water level can not be higher than max!");
        }
    }
}
