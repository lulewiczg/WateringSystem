package com.github.lulewiczg.watering.config.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Water tank configuration.
 */
@Data
@Validated
@RequiredArgsConstructor
public class TankConfig {

    @Valid
    @Min(0)
    private Integer volume;

    @Valid
    private String sensorId;

    @Valid
    @NotNull
    private String valveId;

    @Valid
    @NotNull
    private TankType type;

    @EqualsAndHashCode.Exclude
    private WaterLevelSensorConfig sensor;

    @EqualsAndHashCode.Exclude
    private ValveConfig valve;

    public TankConfig(Integer volume, String sensorId, String valveId, TankType type) {
        this.volume = volume;
        this.sensorId = sensorId;
        this.valveId = valveId;
        this.type = type;
    }
}
