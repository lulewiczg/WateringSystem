package com.github.lulewiczg.watering.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lulewiczg.watering.config.dto.Steerable;
import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.config.dto.ValveConfig;
import com.github.lulewiczg.watering.config.dto.WaterLevelSensorConfig;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for holding system configuration.
 */
@Data
@Log4j2
@Validated
@RequiredArgsConstructor
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "config.system")
public class AppConfig {

    @Valid
    @NotEmpty
    private final List<TankConfig> tanks;

    @Valid
    @NotEmpty
    private final List<ValveConfig> valves;

    @Valid
    @NotNull
    private final List<WaterLevelSensorConfig> sensors;

    @JsonIgnore
    @Value("${app.config.appConfig.runPostConstruct:true}")
    private boolean runPostConstruct;

    @PostConstruct
    void postConstruct() {
        if (runPostConstruct) {
            validate();
        }
        log.info(() -> "Tanks config: " + tanks);
        log.info(() -> "Valves config: " + valves);
        log.info(() -> "Sensors config: " + sensors);
    }

    void validate() {
        tanks.forEach(this::validate);
        validatePins();
    }

    private void validate(TankConfig tank) {
        ValveConfig valve = valves.stream().filter(i -> i.getId().equals(tank.getValveId())).findFirst()
                .orElseThrow(() -> new IllegalStateException("Can not find valve for tank: " + tank.getId()));
        tank.setValve(valve);

        if (tank.getSensorId() != null) {
            WaterLevelSensorConfig waterLevelSensor = sensors.stream().filter(i -> i.getId().equals(tank.getSensorId())).findAny()
                    .orElseThrow(() -> new IllegalStateException("Can not find water level sensor for tank: " + tank.getId()));
            waterLevelSensor.validate();
            tank.setSensor(waterLevelSensor);
        }
    }

    private void validatePins() {
        List<String> usedPins = new ArrayList<>();
        valves.forEach(i -> validatePin(usedPins, i));
        sensors.forEach(i -> validatePin(usedPins, i));
    }

    private void validatePin(List<String> usedPins, Steerable i) {
        String name = i.getPinName();
        if (usedPins.contains(name)) {
            throw new IllegalStateException("Pin already in use: " + name);
        }
        Pin pin = RaspiPin.getPinByName(name);
        if (pin == null) {
            throw new IllegalStateException("Could not find pin: " + name);
        }
        i.setPin(pin);
        usedPins.add(name);
    }


}
