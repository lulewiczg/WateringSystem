package com.github.lulewiczg.watering.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lulewiczg.watering.config.dto.*;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.validation.annotation.Validated;

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

    @Valid
    private final List<PumpConfig> pumps;

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
        if (pumps != null) {
            validatePumps();
        }
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
        if (tank.getPumpId() != null) {
            if (pumps == null) {
                throw new IllegalStateException("No pumps declared!");
            }
            PumpConfig pump = pumps.stream().filter(i -> i.getId().equals(tank.getPumpId())).findFirst()
                    .orElseThrow(() -> new IllegalStateException("Can not find pump for tank: " + tank.getId()));
            tank.setPump(pump);
        }
    }

    private void validatePins() {
        List<Integer> usedPins = new ArrayList<>();
        valves.forEach(i -> validatePin(usedPins, i));
        if (sensors != null) {
            sensors.forEach(i -> validateSensorPin(usedPins, i));
        }
        if (pumps != null) {
            pumps.forEach(i -> validatePin(usedPins, i));
        }
    }

    private void validateAddresses() {
        if (sensors != null) {
            long count = sensors.stream().map(WaterLevelSensorConfig::getAddress).distinct().count();
            if (count != sensors.size()) {
                throw new IllegalStateException("Duplicated sensor addresses found!");
            }
        }
    }

    private void validatePin(List<Integer> usedPins, PinnableConfig config) {
        Integer pin = config.getPin();
        if (pin == null) {
            return;
        }
        validatePin(usedPins, pin);
        config.setPin(pin);
        usedPins.add(pin);
    }

    private void validateSensorPin(List<Integer> usedPins, WaterLevelSensorConfig sensor) {
        Integer pin = sensor.getPowerControlPin();
        if (pin == null) {
            return;
        }
        validatePin(usedPins, pin);
        sensor.setPowerControlPin(pin);
    }

    private void validatePin(List<Integer> usedPins, Integer pin) {
        if (pin < 0 || pin > 40) {
            throw new IllegalStateException("Invalid pin: " + pin);
        }
        if (usedPins.contains(pin)) {
            throw new IllegalStateException("Pin already in use: " + pin);
        }
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

    private void validatePumps() {
        Optional<PumpConfig> incorrect = pumps.stream().filter(i -> tanks.stream().noneMatch(j -> j.getPump() != null && j.getPump().getId().equals(i.getId()))).findFirst();
        if (incorrect.isPresent()) {
            throw new IllegalStateException(String.format("Pump %s has no mapping!", incorrect.get().getId()));
        }
    }
}
