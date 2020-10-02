package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import(WaterLevelReadAction.class)
@ExtendWith(SpringExtension.class)
class WaterLevelReadActionTest {

    @MockBean
    private IOService service;

    @Autowired
    private WaterLevelReadAction action;

    @Test
    void testAction() {
        when(service.analogRead(RaspiPin.GPIO_01)).thenReturn(12.34);
        Sensor sensor = new Sensor(null, null, 12, RaspiPin.GPIO_01);

        Double result = action.doAction(sensor);
        assertEquals(12.34, result);
    }
}