package com.github.lulewiczg.watering.service.io;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("testIo")
@Import(IOServiceImpl.class)
@ExtendWith(SpringExtension.class)
class IOServiceImplTest {

    @Autowired
    private IOServiceImpl ioService;

    @MockBean
    private GpioController gpioController;

    @Mock
    private GpioPinDigitalOutput pin;

    @Mock
    private GpioPinDigitalOutput pin2;

    @Test
    @DirtiesContext
    void testToggleOn() {
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, RaspiPin.GPIO_00.getName(), PinState.LOW)).thenReturn(pin);

        ioService.toggleOn(RaspiPin.GPIO_00);

        verify(pin).setShutdownOptions(true, PinState.LOW);
        verify(pin).high();
    }

    @Test
    @DirtiesContext
    void testToggleOnMultipleTimes() {
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, RaspiPin.GPIO_00.getName(), PinState.LOW)).thenReturn(pin);
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, RaspiPin.GPIO_01.getName(), PinState.LOW)).thenReturn(pin2);

        ioService.toggleOn(RaspiPin.GPIO_00);
        ioService.toggleOn(RaspiPin.GPIO_01);
        ioService.toggleOn(RaspiPin.GPIO_00);

        verify(pin).setShutdownOptions(true, PinState.LOW);
        verify(pin, times(2)).high();
        verify(pin2).setShutdownOptions(true, PinState.LOW);
        verify(pin2).high();
    }

    @Test
    @DirtiesContext
    void testToggleOff() {
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, RaspiPin.GPIO_00.getName(), PinState.LOW)).thenReturn(pin);

        ioService.toggleOff(RaspiPin.GPIO_00);

        verify(pin).setShutdownOptions(true, PinState.LOW);
        verify(pin).low();
    }

    @Test
    @DirtiesContext
    void testToggleOffMultipleTimes() {
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, RaspiPin.GPIO_00.getName(), PinState.LOW)).thenReturn(pin);
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, RaspiPin.GPIO_01.getName(), PinState.LOW)).thenReturn(pin2);

        ioService.toggleOff(RaspiPin.GPIO_00);
        ioService.toggleOff(RaspiPin.GPIO_01);
        ioService.toggleOff(RaspiPin.GPIO_00);

        verify(pin).setShutdownOptions(true, PinState.LOW);
        verify(pin, times(2)).low();
        verify(pin2).setShutdownOptions(true, PinState.LOW);
        verify(pin2).low();
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
