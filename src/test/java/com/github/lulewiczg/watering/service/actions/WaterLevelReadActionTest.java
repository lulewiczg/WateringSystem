package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import(WaterLevelReadAction.class)
@ExtendWith(SpringExtension.class)
class WaterLevelReadActionTest {

    @MockBean
    private IOService service;

    @MockBean
    private AppState state;

    @Autowired
    private WaterLevelReadAction action;

    @Test
    void testAction() {
        when(service.analogRead(RaspiPin.GPIO_01)).thenReturn(12.34);
        Sensor sensor = new Sensor("test", null, null, 12, RaspiPin.GPIO_01);

        ActionResultDto<Double> result = action.doAction(new ActionDto(), sensor);
        assertEquals(12.34, result.getResult());
    }

    @Test
    void testActionEnabled() {
        when(state.getTanks()).thenReturn(List.of(new Tank(), new Tank(null, null, new Sensor(), null)));

        assertTrue(action.isEnabled());
    }

    @Test
    void testActionDisabled() {
        when(state.getTanks()).thenReturn(List.of(new Tank()));

        assertFalse(action.isEnabled());
    }
}
