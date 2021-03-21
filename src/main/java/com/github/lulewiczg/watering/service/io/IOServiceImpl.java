package com.github.lulewiczg.watering.service.io;

import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.config.dto.WaterLevelSensorConfig;
import com.github.lulewiczg.watering.service.ina219.INA219;
import com.github.lulewiczg.watering.service.ina219.enums.Adc;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
import com.github.lulewiczg.watering.service.ina219.enums.Pga;
import com.github.lulewiczg.watering.service.ina219.enums.VoltageRange;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for GPIO communication.
 */
@Log4j2
@Service
@ConditionalOnMissingBean(MasterConfig.class)
@ConditionalOnExpression("!${com.github.lulewiczg.watering.mockedIO:false}")
public class IOServiceImpl implements IOService {

    private static final String ERR = "Not yet implemented!";

    private final GpioController gpioController;

    private final Map<Pin, GpioPinDigitalOutput> pins = new HashMap<>();

    private final Map<Address, INA219> sensors = new HashMap<>();

    @SneakyThrows
    public IOServiceImpl(GpioController gpioController, INA219Resolver resolver, AppConfig config) {
        this.gpioController = gpioController;
        config.getSensors().stream().map(WaterLevelSensorConfig::getAddress).forEach(i -> sensors.put(i, resolver.get(i)));
    }

    @Override
    public void toggleOn(Pin pin) {
        GpioPinDigitalOutput gpioPin = getPin(pin);
        gpioPin.high();
        log.trace("Pin {} is set to ON", pin);
    }

    @Override
    public void toggleOff(Pin pin) {
        GpioPinDigitalOutput gpioPin = getPin(pin);
        gpioPin.low();
        log.trace("Pin {} is set to OFF", pin);
    }

    @Override
    public boolean readPin(Pin pin) {
        throw new IllegalStateException(ERR);
    }

    @Override
    public double analogRead(Address address) {
        INA219 ina219 = sensors.get(address);
        if (ina219 == null) {
            throw new IllegalStateException("No sensor found for address: " + address);
        }
        return ina219.getCurrent();
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
