package com.github.lulewiczg.watering.config.dto;

import com.github.lulewiczg.watering.service.ina219.enums.Address;
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
import javax.validation.constraints.NotNull;

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

    private String powerControlPinName;

    @EqualsAndHashCode.Exclude
    private Pin powerControlPin;

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

    public WaterLevelSensorConfig(String id, Integer minLevel, Integer maxLevel, Address address, String powerControlPinName,
                                  int minResistance, int maxResistance, double voltage) {
        this.id = id;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.address = address;
        this.powerControlPinName = powerControlPinName;
        this.minResistance = minResistance;
        this.maxResistance = maxResistance;
        this.voltage = voltage;
    }
}
