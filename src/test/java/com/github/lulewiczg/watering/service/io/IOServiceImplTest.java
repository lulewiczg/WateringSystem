package com.github.lulewiczg.watering.service.io;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.service.ina219.INA219;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.pi4j.context.Context;
import com.pi4j.context.ContextProperties;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("testIo")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class IOServiceImplTest {

    private IOServiceImpl ioService;

    @MockitoBean
    private Context pi4jContext;

    @MockitoBean
    private AppConfig config;

    @MockitoBean
    private IOServiceImpl.INA219Resolver resolver;

    @Mock
    private INA219 ina219;

    @Mock
    private INA219 ina2192;

    @Mock
    private DigitalOutput pin;

    @Mock
    private DigitalOutput pin2;

    @Mock
    private ContextProperties contextProperties;

    @BeforeEach
    void setUp() {
        when(pi4jContext.properties()).thenReturn(contextProperties);
    }

    @Test
    @DirtiesContext
    void testToggleOn() {
        doReturn(pin).when(pi4jContext).create(any(DigitalOutputConfig.class));
        ioService = new IOServiceImpl(pi4jContext, null, config, 3, 100);

        ioService.toggleOn(0);

        verify(pin).low();
    }

    @Test
    @DirtiesContext
    void testToggleOnMultipleTimes() {
        doReturn(pin, pin2).when(pi4jContext).create(any(DigitalOutputConfig.class));
        ioService = new IOServiceImpl(pi4jContext, null, config, 3, 100);

        ioService.toggleOn(0);
        ioService.toggleOn(1);
        ioService.toggleOn(0);

        verify(pin, times(2)).low();
        verify(pin2).low();
    }

    @Test
    @DirtiesContext
    void testToggleOff() {
        doReturn(pin).when(pi4jContext).create(any(DigitalOutputConfig.class));
        ioService = new IOServiceImpl(pi4jContext, null, config, 3, 100);

        ioService.toggleOff(0);

        verify(pin).high();
    }

    @Test
    @DirtiesContext
    void testToggleOffMultipleTimes() {
        doReturn(pin, pin2).when(pi4jContext).create(any(DigitalOutputConfig.class));
        ioService = new IOServiceImpl(pi4jContext, null, config, 3, 100);

        ioService.toggleOff(0);
        ioService.toggleOff(1);
        ioService.toggleOff(0);

        verify(pin, times(2)).high();
        verify(pin2).high();
    }

    @Test
    void testRead() {
        ioService = new IOServiceImpl(pi4jContext, null, config, 3, 100);

        assertThrows(IllegalStateException.class, () -> ioService.readPin(0));
    }

    @Test
    void testAnalogRead() {
        when(config.getSensors()).thenReturn(List.of(TestUtils.Config.SENSOR2));
        when(resolver.get(Address.ADDR_41)).thenReturn(ina219);
        when(ina219.getCurrent()).thenReturn(12.34);
        ioService = new IOServiceImpl(pi4jContext, resolver, config, 3, 100);

        double result = ioService.analogRead(TestUtils.Objects.SENSOR2);

        assertEquals(12.34, result);
    }

    @Test
    void testAnalogReadRetry() {
        when(config.getSensors()).thenReturn(List.of(TestUtils.Config.SENSOR2));
        when(resolver.get(Address.ADDR_41)).thenReturn(ina219);
        when(ina219.getCurrent()).thenReturn(0d, 0d, 12.34);
        ioService = new IOServiceImpl(pi4jContext, resolver, config, 3, 100);

        double result = ioService.analogRead(TestUtils.Objects.SENSOR2);

        assertEquals(12.34, result);
        verify(ina219, times(3)).getCurrent();
    }

    @Test
    void testAnalogReadRetryFail() {
        when(config.getSensors()).thenReturn(List.of(TestUtils.Config.SENSOR2));
        when(resolver.get(Address.ADDR_41)).thenReturn(ina219);
        when(ina219.getCurrent()).thenReturn(0d, 0d, 0d);
        ioService = new IOServiceImpl(pi4jContext, resolver, config, 3, 100);

        double result = ioService.analogRead(TestUtils.Objects.SENSOR2);

        assertEquals(0, result);
        verify(ina219, times(3)).getCurrent();
    }

    @Test
    void testAnalogReadWithPowerControl() {
        doReturn(pin).when(pi4jContext).create(any(DigitalOutputConfig.class));
        when(config.getSensors()).thenReturn(List.of(TestUtils.Config.SENSOR));
        when(resolver.get(Address.ADDR_40)).thenReturn(ina219);
        when(ina219.getCurrent()).thenReturn(12.34);
        ioService = new IOServiceImpl(pi4jContext, resolver, config, 3, 100);

        double result = ioService.analogRead(TestUtils.Objects.SENSOR);

        assertEquals(12.34, result);
        InOrder inOrder = inOrder(pin, ina219);
        inOrder.verify(pin).low();
        inOrder.verify(ina219).getCurrent();
        inOrder.verify(pin).high();
    }

    @Test
    void testAnalogReadMultipleSensors() {
        doReturn(pin).when(pi4jContext).create(any(DigitalOutputConfig.class));
        when(config.getSensors()).thenReturn(List.of(TestUtils.Config.SENSOR, TestUtils.Config.SENSOR2));
        when(resolver.get(Address.ADDR_40)).thenReturn(ina219);
        when(resolver.get(Address.ADDR_41)).thenReturn(ina2192);
        when(ina2192.getCurrent()).thenReturn(43.21);
        when(ina2192.getCurrent()).thenReturn(12.34);
        ioService = new IOServiceImpl(pi4jContext, resolver, config, 3, 100);
        Sensor sensor = new Sensor("id", null, 0, 100, Address.ADDR_41, 10, 10, 11, 12);

        double result = ioService.analogRead(sensor);

        assertEquals(12.34, result);
    }

    @Test
    void testAnalogReadInvalidAddress() {
        when(config.getSensors()).thenReturn(List.of(TestUtils.Config.SENSOR));
        when(resolver.get(Address.ADDR_40)).thenReturn(ina219);
        ioService = new IOServiceImpl(pi4jContext, resolver, config, 3, 100);

        String message = assertThrows(IllegalStateException.class, () -> ioService.analogRead(TestUtils.Objects.SENSOR2)).getMessage();

        assertEquals("No sensor found for address: ADDR_41", message);
    }

}
