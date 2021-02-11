package com.github.lulewiczg.watering.service.io;

import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("testIo")
@Import(IOServiceImpl.class)
@ExtendWith(SpringExtension.class)
class IOServiceImplTest {

    @Autowired
    private IOServiceImpl ioService;

    @Test
    void testToggleOn() {
        assertThrows(IllegalStateException.class, () -> ioService.toggleOn(RaspiPin.GPIO_00));
    }

    @Test
    void testToggleOff() {
        assertThrows(IllegalStateException.class, () -> ioService.toggleOff(RaspiPin.GPIO_00));
    }

    @Test
    void testRead() {
        assertThrows(IllegalStateException.class, () -> ioService.readPin(RaspiPin.GPIO_00));
    }

    @Test
    void testAnalogRead() {
        assertThrows(IllegalStateException.class, () -> ioService.analogRead(RaspiPin.GPIO_00));
    }
}
