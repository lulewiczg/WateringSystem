package com.github.lulewiczg.watering.service.io;

import com.github.lulewiczg.watering.service.ina219.enums.Address;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.Pin;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Mock service for GPIO communication.
 */
@Log4j2
@Service
@Generated
@RequiredArgsConstructor
@ConditionalOnMissingBean(IOServiceImpl.class)
public class IOServiceMock implements IOService {

    private final AppState state;

    @Override
    public void toggleOn(Pin pin) {
        log.info("Mock toggle ON for pin {}", pin);
    }

    @Override
    public void toggleOff(Pin pin) {
        log.info("Mock toggle OFF for pin {}", pin);
    }

    @Override
    public boolean readPin(Pin pin) {
        log.info("Read for pin {}", pin);
        Optional<Valve> first = state.getTanks().stream().map(Tank::getValve).filter(i -> i.getPin().equals(pin)).findFirst();
        Valve tmpValve = state.getOutputs().stream().filter(i -> i.getPin().equals(pin)).findFirst().orElse(null);
        Valve valve = first.orElse(tmpValve);
        if (valve != null) {
            return valve.isOpen();
        }
        return false;
    }

    @Override
    public double analogRead(Address address) {
        log.info("Analog read for address {}", address);
        return 0;
    }
}
