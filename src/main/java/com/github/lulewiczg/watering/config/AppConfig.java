package com.github.lulewiczg.watering.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.config.dto.ValveConfig;
import com.github.lulewiczg.watering.config.dto.ValveType;
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
import java.util.Optional;

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
    @Value("#{${app.config.appConfig.runPostConstruct:true} && '${com.github.lulewiczg.watering.role:}' != 'master'}")
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
        validateAddresses();
        validateValves();
    }

    private void validate(TankConfig tank) {
        if (tank.getValveId() != null) {
            ValveConfig valve = valves.stream().filter(i -> i.getId().equals(tank.getValveId())).findFirst()
                    .orElseThrow(() -> new IllegalStateException("Can not find valve for tank: " + tank.getId()));
            tank.setValve(valve);
        }

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
        sensors.forEach(i -> validateSensorPin(usedPins, i));
    }

    private void validateAddresses() {
        long count = sensors.stream().map(WaterLevelSensorConfig::getAddress).distinct().count();
        if (count != sensors.size()) {
            throw new IllegalStateException("Duplicated sensor addresses found!");
        }
    }

    private void validatePin(List<String> usedPins, ValveConfig valve) {
        String name = valve.getPinName();
        if (usedPins.contains(name)) {
            throw new IllegalStateException("Pin already in use: " + name);
        }
        Pin pin = RaspiPin.getPinByName(name);
        if (pin == null) {
            throw new IllegalStateException("Could not find pin: " + name);
        }
        valve.setPin(pin);
        usedPins.add(name);
    }

    private void validateSensorPin(List<String> usedPins, WaterLevelSensorConfig sensor) {
        String name = sensor.getPowerControlPinName();
        if (name == null || name.isEmpty()) {
            return;
        }
        if (usedPins.contains(name)) {
            throw new IllegalStateException("Pin already in use: " + name);
        }
        Pin pin = RaspiPin.getPinByName(name);
        if (pin == null) {
            throw new IllegalStateException("Could not find pin: " + name);
        }
        sensor.setPowerControlPin(pin);
    }

    private void validateValves() {
        Optional<ValveConfig> incorrect = valves.stream().filter(i -> i.getType() == ValveType.INPUT && i.isOverflowOutput()).findFirst();
        if (incorrect.isPresent()) {
            throw new IllegalStateException(String.format("Input valve %s cannot be input and overflow valve!", incorrect.get().getId()));
        }
        Optional<ValveConfig> invalidInput = valves.stream().filter(i -> i.getType() == ValveType.INPUT && i.getWateringTime() != null).findFirst();
        if (invalidInput.isPresent()) {
            throw new IllegalStateException(String.format("Input valve %s cannot have watering time!", invalidInput.get().getId()));
        }
    }
}
