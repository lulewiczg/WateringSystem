package com.github.lulewiczg.watering.config.dto;

import com.github.lulewiczg.watering.service.ina219.enums.Address;
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

    @EqualsAndHashCode.Exclude
    private Address address;

    public void validate() {
        if (minLevel > maxLevel) {
            throw new IllegalStateException("Min water level can not be higher than max!");
        }
    }
}
