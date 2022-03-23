package com.github.lulewiczg.watering.config.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

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

    private String sensorId;

    private String valveId;

    private String pumpId;

    @NotNull
    private TankType type;

    @EqualsAndHashCode.Exclude
    private WaterLevelSensorConfig sensor;

    @EqualsAndHashCode.Exclude
    private ValveConfig valve;

    @EqualsAndHashCode.Exclude
    private PumpConfig pump;

    public TankConfig(String id, Integer volume, String sensorId, String valveId, String pumpId, TankType type) {
        this.id = id;
        this.volume = volume;
        this.sensorId = sensorId;
        this.valveId = valveId;
        this.pumpId = pumpId;
        this.type = type;
    }

}
