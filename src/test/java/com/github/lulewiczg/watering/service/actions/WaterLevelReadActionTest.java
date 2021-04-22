package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.service.io.SensorService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Tank;
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

    @MockBean
    private SensorService sensorService;

    @Autowired
    private WaterLevelReadAction action;

    @Test
    void testAction() {
        when(service.analogRead(TestUtils.SENSOR)).thenReturn(12.34);
        when(sensorService.calculateWaterLevel(12.34, TestUtils.SENSOR)).thenReturn(43.21);

        Double result = action.doAction(new ActionDto(), TestUtils.SENSOR);
        assertEquals(43.21, result);
    }

    @Test
    void testActionEnabled() {
        when(state.getTanks()).thenReturn(List.of(TestUtils.TANK));

        assertTrue(action.isEnabled());
    }

    @Test
    void testActionDisabled() {
        when(state.getTanks()).thenReturn(List.of(new Tank()));

        assertFalse(action.isEnabled());
    }
}
