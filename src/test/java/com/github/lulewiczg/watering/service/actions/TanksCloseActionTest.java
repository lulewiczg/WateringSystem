package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.github.lulewiczg.watering.state.dto.WaterSource;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Import(TanksCloseAction.class)
@ExtendWith(SpringExtension.class)
class TanksCloseActionTest {

    @MockBean
    private ValveCloseAction closeAction;

    @MockBean
    private AppState state;

    @MockBean
    private ActionRunner runner;

    @Autowired
    private TanksCloseAction action;

    @Test
    void testAction() {
        Valve valve = new Valve("test", "test", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Valve valve2 = new Valve("test2", "test2", ValveType.OUTPUT, true, RaspiPin.GPIO_01);
        Valve valve3 = new Valve("test3", "test3", ValveType.INPUT, true, RaspiPin.GPIO_03);
        Valve valve4 = new Valve("test4", "test4", ValveType.INPUT, true, RaspiPin.GPIO_04);
        List<Valve> valves = List.of(valve, valve2);
        List<Tank> tanks = List.of(new Tank("tank", 1, null, valve3));
        List<WaterSource> taps = List.of(new WaterSource("tap", valve4));
        when(state.getOutputs()).thenReturn(valves);
        when(state.getTanks()).thenReturn(tanks);
        when(state.getTaps()).thenReturn(taps);
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        ActionDto actionDto = new ActionDto("test");

        action.doAction(actionDto, null);

        verify(runner).run("test.", closeAction, valve3);
        verify(runner, never()).run("test.", closeAction, valve);
        verify(runner, never()).run("test.", closeAction, valve2);
        verify(runner, never()).run("test.", closeAction, valve4);
    }

    @Test
    void testActionNestedFail() {
        Valve valve = new Valve("test", "test", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Valve valve2 = new Valve("test2", "test2", ValveType.OUTPUT, true, RaspiPin.GPIO_01);
        Valve valve3 = new Valve("test3", "test3", ValveType.INPUT, true, RaspiPin.GPIO_03);
        Valve valve4 = new Valve("test4", "test4", ValveType.INPUT, true, RaspiPin.GPIO_04);
        List<Valve> valves = List.of(valve, valve2);
        List<Tank> tanks = List.of(new Tank("tank", 1, null, valve3));
        List<WaterSource> taps = List.of(new WaterSource("tap", valve4));
        when(state.getOutputs()).thenReturn(valves);
        when(state.getTanks()).thenReturn(tanks);
        when(state.getTaps()).thenReturn(taps);
        when(runner.run(eq("test."), eq(closeAction), any())).thenThrow(new ActionException("id", "error"));
        ActionDto actionDto = new ActionDto("test");

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", closeAction, valve3);
        verify(runner, never()).run("test.", closeAction, valve);
        verify(runner, never()).run("test.", closeAction, valve2);
        verify(runner, never()).run("test.", closeAction, valve4);
    }
}
