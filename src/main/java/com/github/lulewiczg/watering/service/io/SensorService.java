package com.github.lulewiczg.watering.service.io;

import com.github.lulewiczg.watering.state.dto.Sensor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service for sensors logic.
 */
@Log4j2
@Service
public class SensorService {

    /**
     * Calculates water level from read current. It is read from base resistance and next level steps from Ohm's law.
     *
     * @param current current
     * @param sensor  sensor
     * @return percentage water level
     */
    public double calculateWaterLevel(double current, Sensor sensor) {
        double resistance = sensor.getVoltage() / current;
        log.info("Sensor resistance: {}", resistance);
        double activeResistance = resistance - sensor.getPassiveResistance();
        log.info("Active resistance: {}", activeResistance);
        long resistorNumber = Math.round(activeResistance / sensor.getStepResistance());
        log.info("Resistor: {}", resistorNumber);
        double result = 100 - (resistorNumber / (double) sensor.getResistorsNumber() * 100d);
        log.info("Result: {}", result);
        result = fixErrors(result);
        return result;
    }

    private double fixErrors(double result) {
        if (result < 0) {
            log.error("Invalid level value {}!", result);
            result = 0;
        }
        if (result > 100) {
            log.error("Invalid level value {}!", result);
            result = 100;
        }
        return result;
    }

}
