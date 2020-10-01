package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.config.dto.TankType;
import com.github.lulewiczg.watering.config.dto.ValveConfig;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.AppState;
import com.github.lulewiczg.watering.state.Tank;
import com.github.lulewiczg.watering.state.Valve;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Import(TapsOpenAction.class)
@ExtendWith(SpringExtension.class)
class TapsOpenActionTest {

    @MockBean
    private ValveOpenAction openAction;

    @MockBean
    private AppState state;

    @Autowired
    private TapsOpenAction action;

    @Test
    void testAction() {
        Valve valve = new Valve(new ValveConfig("test", ValveType.OUTPUT, "0", true, RaspiPin.GPIO_00));
        Valve valve2 = new Valve(new ValveConfig("test", ValveType.OUTPUT, "1", true, RaspiPin.GPIO_01));
        List<Tank> tanks = List.of(new Tank(new TankConfig(1, null, null, TankType.UNLIMITED, null, new ValveConfig("test", ValveType.INPUT, "3", true, RaspiPin.GPIO_03))),
                new Tank(new TankConfig(1, null, null, TankType.DEFAULT, null, new ValveConfig("test", ValveType.INPUT, "4", true, RaspiPin.GPIO_04))));
        when(state.getOutputValves()).thenReturn(List.of(valve, valve2));
        when(state.getTanks()).thenReturn(tanks);

        action.doAction(null);

        Valve valve3 = new Valve(new ValveConfig("test", ValveType.INPUT, "3", true, RaspiPin.GPIO_03));
        Valve valve4 = new Valve(new ValveConfig("test", ValveType.INPUT, "4", true, RaspiPin.GPIO_04));
        verify(openAction).doAction(valve3);
        verify(openAction, never()).doAction(valve);
        verify(openAction, never()).doAction(valve2);
        verify(openAction, never()).doAction(valve4);
    }

}