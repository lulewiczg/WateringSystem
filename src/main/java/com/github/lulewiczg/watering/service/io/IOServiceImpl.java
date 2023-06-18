package com.github.lulewiczg.watering.service.io;

import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.config.dto.WaterLevelSensorConfig;
import com.github.lulewiczg.watering.service.ina219.INA219;
import com.github.lulewiczg.watering.service.ina219.enums.Adc;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
import com.github.lulewiczg.watering.service.ina219.enums.Pga;
import com.github.lulewiczg.watering.service.ina219.enums.VoltageRange;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for GPIO communication.
 */
@Log4j2
@Service
@ConditionalOnMissingBean(MasterConfig.class)
@ConditionalOnExpression("!${com.github.lulewiczg.watering.mockedIO:false}")
public class IOServiceImpl implements IOService {

    private final int numberOfReads;

    private final int readInterval;

    private static final String ERR = "Not yet implemented!";

    private final GpioController gpioController;

    private final Map<Pin, GpioPinDigitalOutput> pins = new HashMap<>();

    private final Map<Address, INA219> sensors = new EnumMap<>(Address.class);

    @SneakyThrows
    public IOServiceImpl(GpioController gpioController, INA219Resolver resolver, AppConfig config,
                         @Value("${com.github.lulewiczg.watering.io.numberOfReads:100}") int numberOfReads,
                         @Value("${com.github.lulewiczg.watering.io.retryWait:10}") int readInteval) {
        this.gpioController = gpioController;
        this.numberOfReads = numberOfReads;
        this.readInterval = readInteval;
        config.getSensors().stream().map(WaterLevelSensorConfig::getAddress).forEach(i -> sensors.put(i, resolver.get(i)));
    }

    @Override
    public void toggleOn(Pin pin) {
        GpioPinDigitalOutput gpioPin = getPin(pin);
        gpioPin.low();
        log.trace("Pin {} is set to ON", pin);
    }

    @Override
    public void toggleOff(Pin pin) {
        GpioPinDigitalOutput gpioPin = getPin(pin);
        gpioPin.high();
        log.trace("Pin {} is set to OFF", pin);
    }

    @Override
    public boolean readPin(Pin pin) {
        throw new IllegalStateException(ERR);
    }

    @SneakyThrows
    @Override
    public double analogRead(Sensor sensor) {
        Address address = sensor.getAddress();
        INA219 ina219 = sensors.get(address);
        if (ina219 == null) {
            throw new IllegalStateException("No sensor found for address: " + address);
        }
        Pin powerControlPin = sensor.getPowerControlPin();
        if (powerControlPin != null) {
            toggleOn(powerControlPin);
            Thread.sleep(2000);
            double current = readCurrent(address, ina219);
            Thread.sleep(100);
            toggleOff(powerControlPin);
            return current;
        }
        return readCurrent(address, ina219);
    }

    private GpioPinDigitalOutput getPin(Pin pin) {
        GpioPinDigitalOutput existing = pins.get(pin);
        if (existing != null) {
            return existing;
        }
        GpioPinDigitalOutput gpioPin = gpioController.provisionDigitalOutputPin(pin, pin.getName(), PinState.LOW);
        gpioPin.setShutdownOptions(true, PinState.LOW);
        pins.put(pin, gpioPin);
        return gpioPin;
    }

    @SneakyThrows
    private double readCurrent(Address address, INA219 ina219) {
        int reads = 0;
        List<Double> results = new ArrayList<>();
        while (reads < numberOfReads) {
            double current = ina219.getCurrent();
            log.trace("Read current {} for address {}", current, address);
            if (current > 0) {
                reads++;
                results.add(current);
            } else {
                log.trace("Invalid current, retrying...");
            }
            Thread.sleep(readInterval);
        }
        double avgCurrent = results.stream().reduce(Double::sum).orElse(0d) / (double) results.size();
        if (avgCurrent <= 0) {
            log.error("Invalid current value!");
            avgCurrent = 0;
        }

        return avgCurrent;
    }

    /**
     * Resolver for INA219
     */
    @Component
    static class INA219Resolver {

        @SneakyThrows
        INA219 get(Address address) {
            return new INA219(address, 0.1, 1, VoltageRange.V16, Pga.GAIN_1, Adc.SAMPLES_128, Adc.SAMPLES_128);
        }
    }
}
