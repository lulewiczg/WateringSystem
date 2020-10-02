package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
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
@Import(TanksCloseAction.class)
@ExtendWith(SpringExtension.class)
class TanksCloseActionTest {

    @MockBean
    private ValveCloseAction closeAction;

    @MockBean
    private AppState state;

    @Autowired
    private TanksCloseAction action;

    @Test
    void testAction() {
        Valve valve = new Valve("test", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Valve valve2 = new Valve("test2", ValveType.OUTPUT, true, RaspiPin.GPIO_01);
        Valve valve3 = new Valve("test3", ValveType.INPUT, true, RaspiPin.GPIO_03);
        Valve valve4 = new Valve("test4", ValveType.INPUT, true, RaspiPin.GPIO_04);
        List<Valve> valves = List.of(valve, valve2);
        List<Tank> tanks = List.of(new Tank(1, null, valve3));
        List<Tank> taps = List.of(new Tank(1, null, valve4));
        when(state.getOutputs()).thenReturn(valves);
        when(state.getTanks()).thenReturn(tanks);
        when(state.getTaps()).thenReturn(taps);

        action.doAction(null);

        verify(closeAction).doAction(valve3);
        verify(closeAction, never()).doAction(valve);
        verify(closeAction, never()).doAction(valve2);
        verify(closeAction, never()).doAction(valve4);
    }

}