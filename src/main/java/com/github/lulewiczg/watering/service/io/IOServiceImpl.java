package com.github.lulewiczg.watering.service.io;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for GPIO communication.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
@ConditionalOnExpression("!${com.github.lulewiczg.watering.mockedIO:false}")
public class IOServiceImpl implements IOService {

    private static final String ERR = "Not yet implemented!";

    private final GpioController gpioController;

    private final Map<Pin, GpioPinDigitalOutput> pins = new HashMap<>();

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
    public double analogRead(Pin pin) {
        throw new IllegalStateException(ERR);
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

}
