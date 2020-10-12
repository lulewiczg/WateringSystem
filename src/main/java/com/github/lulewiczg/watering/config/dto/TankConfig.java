package com.github.lulewiczg.watering.config.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Water tank configuration.
 */
@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class TankConfig {

    @NotEmpty
    private String id;

    @Min(0)
    private Integer volume;

    @Valid
    private String sensorId;

    @NotEmpty
    private String valveId;

    @NotNull
    private TankType type;

    @EqualsAndHashCode.Exclude
    private WaterLevelSensorConfig sensor;

    @EqualsAndHashCode.Exclude
    private ValveConfig valve;

    public TankConfig(String id, Integer volume, String sensorId, String valveId, TankType type) {
        this.id = id;
        this.volume = volume;
        this.sensorId = sensorId;
        this.valveId = valveId;
        this.type = type;
    }

}
