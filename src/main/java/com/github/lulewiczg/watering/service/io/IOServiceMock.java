package com.github.lulewiczg.watering.service.io;

import com.pi4j.io.gpio.Pin;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * Mock service for GPIO communication.
 */
@Log4j2
@Service
@ConditionalOnMissingBean(IOServiceImpl.class)
public class IOServiceMock implements IOService {

    @Override
    public void toggleOn(Pin pin) {
        log.info("Mock toggle ON for pin {}", pin);
    }

    @Override
    public void toggleOff(Pin pin) {
        log.info("Mock toggle OFF for pin {}", pin);
    }

    @Override
    public double analogRead(Pin pin) {
        log.info("Analog read for pin {}", pin);
        return 0;
    }
}
