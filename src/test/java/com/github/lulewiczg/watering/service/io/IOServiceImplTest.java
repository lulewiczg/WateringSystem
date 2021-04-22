package com.github.lulewiczg.watering.service.io;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.config.dto.*;
import com.github.lulewiczg.watering.service.ina219.INA219;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("testIo")
@ExtendWith(SpringExtension.class)
class IOServiceImplTest {

    private IOServiceImpl ioService;

    @MockBean
    private GpioController gpioController;

    @MockBean
    private AppConfig config;

    @MockBean
    private IOServiceImpl.INA219Resolver resolver;

    @Mock
    private INA219 ina219;

    @Mock
    private INA219 ina2192;


    @Mock
    private GpioPinDigitalOutput pin;

    @Mock
    private GpioPinDigitalOutput pin2;

    @Test
    @DirtiesContext
    void testToggleOn() {
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, RaspiPin.GPIO_00.getName(), PinState.LOW)).thenReturn(pin);
        ioService = new IOServiceImpl(gpioController, null, config);

        ioService.toggleOn(RaspiPin.GPIO_00);

        verify(pin).setShutdownOptions(true, PinState.LOW);
        verify(pin).low();
    }

    @Test
    @DirtiesContext
    void testToggleOnMultipleTimes() {
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, RaspiPin.GPIO_00.getName(), PinState.LOW)).thenReturn(pin);
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, RaspiPin.GPIO_01.getName(), PinState.LOW)).thenReturn(pin2);
        ioService = new IOServiceImpl(gpioController, null, config);

        ioService.toggleOn(RaspiPin.GPIO_00);
        ioService.toggleOn(RaspiPin.GPIO_01);
        ioService.toggleOn(RaspiPin.GPIO_00);

        verify(pin).setShutdownOptions(true, PinState.LOW);
        verify(pin, times(2)).low();
        verify(pin2).setShutdownOptions(true, PinState.LOW);
        verify(pin2).low();
    }

    @Test
    @DirtiesContext
    void testToggleOff() {
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, RaspiPin.GPIO_00.getName(), PinState.LOW)).thenReturn(pin);
        ioService = new IOServiceImpl(gpioController, null, config);

        ioService.toggleOff(RaspiPin.GPIO_00);

        verify(pin).setShutdownOptions(true, PinState.LOW);
        verify(pin).high();
    }

    @Test
    @DirtiesContext
    void testToggleOffMultipleTimes() {
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, RaspiPin.GPIO_00.getName(), PinState.LOW)).thenReturn(pin);
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, RaspiPin.GPIO_01.getName(), PinState.LOW)).thenReturn(pin2);
        ioService = new IOServiceImpl(gpioController, null, config);

        ioService.toggleOff(RaspiPin.GPIO_00);
        ioService.toggleOff(RaspiPin.GPIO_01);
        ioService.toggleOff(RaspiPin.GPIO_00);

        verify(pin).setShutdownOptions(true, PinState.LOW);
        verify(pin, times(2)).high();
        verify(pin2).setShutdownOptions(true, PinState.LOW);
        verify(pin2).high();
    }

    @Test
    void testRead() {
        ioService = new IOServiceImpl(gpioController, null, config);

        assertThrows(IllegalStateException.class, () -> ioService.readPin(RaspiPin.GPIO_00));
    }

    @Test
    void testAnalogRead() {
        when(config.getSensors()).thenReturn(List.of(TestUtils.SENSOR_CONFIG));
        when(resolver.get(Address.ADDR_41)).thenReturn(ina219);
        when(ina219.getCurrent()).thenReturn(12.34);
        ioService = new IOServiceImpl(gpioController, resolver, config);

        double result = ioService.analogRead(TestUtils.SENSOR2);

        assertEquals(12.34, result);
    }

    @Test
    void testAnalogReadWithPowerControl() {
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_10, RaspiPin.GPIO_10.getName(), PinState.LOW)).thenReturn(pin);
        when(config.getSensors()).thenReturn(List.of(TestUtils.SENSOR_CONFIG2));
        when(resolver.get(Address.ADDR_40)).thenReturn(ina219);
        when(ina219.getCurrent()).thenReturn(12.34);
        ioService = new IOServiceImpl(gpioController, resolver, config);

        double result = ioService.analogRead(TestUtils.SENSOR);

        assertEquals(12.34, result);
        InOrder inOrder = inOrder(pin, ina219);
        inOrder.verify(pin).low();
        inOrder.verify(ina219).getCurrent();
        inOrder.verify(pin).high();
    }

    @Test
    void testAnalogReadMultipleSensors() {
        when(gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_10, RaspiPin.GPIO_10.getName(), PinState.LOW)).thenReturn(pin);
        when(config.getSensors()).thenReturn(List.of(TestUtils.SENSOR_CONFIG, TestUtils.SENSOR_CONFIG2));
        when(resolver.get(Address.ADDR_40)).thenReturn(ina219);
        when(resolver.get(Address.ADDR_41)).thenReturn(ina2192);
        when(ina2192.getCurrent()).thenReturn(43.21);
        when(ina2192.getCurrent()).thenReturn(12.34);
        ioService = new IOServiceImpl(gpioController, resolver, config);
        Sensor sensor = new Sensor("id", 0, 100, null, Address.ADDR_41, RaspiPin.GPIO_10, 10, 11, 12, 13);

        double result = ioService.analogRead(sensor);

        assertEquals(12.34, result);
    }

    @Test
    void testAnalogReadInvalidAddress() {
        when(config.getSensors()).thenReturn(List.of(TestUtils.SENSOR_CONFIG));
        when(resolver.get(Address.ADDR_40)).thenReturn(ina219);
        when(ina219.getCurrent()).thenReturn(12.34);
        ioService = new IOServiceImpl(gpioController, resolver, config);

        String message = assertThrows(IllegalStateException.class, () -> ioService.analogRead(TestUtils.SENSOR2)).getMessage();

        assertEquals("No sensor found for address: ADDR_41", message);
    }

}
