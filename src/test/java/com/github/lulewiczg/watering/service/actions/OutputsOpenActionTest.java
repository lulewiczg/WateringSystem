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
import org.junit.jupiter.api.AfterEach;
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
@Import(OutputsOpenAction.class)
@ExtendWith(SpringExtension.class)
class OutputsOpenActionTest {

    @MockBean
    private ValveOpenAction openAction;

    @MockBean
    private AppState state;

    @MockBean
    private ActionRunner runner;

    @Autowired
    private OutputsOpenAction action;

    @AfterEach
    void after() {
        verifyNoInteractions(openAction);
    }

    @Test
    void testAction() {
        Valve valve = new Valve("test", "test", ValveType.OUTPUT, true, false, 1L, RaspiPin.GPIO_00);
        Valve valve2 = new Valve("test2", "test2", ValveType.OUTPUT, true, false, 1L, RaspiPin.GPIO_01);
        Valve valve3 = new Valve("test3", "test3", ValveType.INPUT, true, false, null, RaspiPin.GPIO_03);
        Valve valve4 = new Valve("test4", "test4", ValveType.INPUT, true, false, null, RaspiPin.GPIO_04);
        List<Valve> valves = List.of(valve, valve2);
        List<Tank> tanks = List.of(new Tank("tank", 1, null, valve3));
        List<WaterSource> taps = List.of(new WaterSource("tap", valve4));
        when(state.getOutputs()).thenReturn(valves);
        when(state.getTanks()).thenReturn(tanks);
        when(state.getTaps()).thenReturn(taps);
        ActionDto actionDto = new ActionDto("test");
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);

        action.doAction(actionDto, null);

        verify(runner).run("test.", openAction, valve);
        verify(runner).run("test.", openAction, valve2);
        verify(runner, never()).run("test.", openAction, valve3);
        verify(runner, never()).run("test.", openAction, valve4);
    }

    @Test
    void testActionNestedFail() {
        Valve valve = new Valve("test", "test", ValveType.OUTPUT, true, false, 1L, RaspiPin.GPIO_00);
        Valve valve2 = new Valve("test2", "test2", ValveType.OUTPUT, true, false, 1L, RaspiPin.GPIO_01);
        Valve valve3 = new Valve("test3", "test3", ValveType.INPUT, true, false, null, RaspiPin.GPIO_03);
        Valve valve4 = new Valve("test4", "test4", ValveType.INPUT, true, false, null, RaspiPin.GPIO_04);
        List<Valve> valves = List.of(valve, valve2);
        List<Tank> tanks = List.of(new Tank("tank", 1, null, valve3));
        List<WaterSource> taps = List.of(new WaterSource("tap", valve4));
        when(state.getOutputs()).thenReturn(valves);
        when(state.getTanks()).thenReturn(tanks);
        when(state.getTaps()).thenReturn(taps);
        ActionDto actionDto = new ActionDto("test");
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.ERROR_RESULT);

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", openAction, valve);
        verify(runner, never()).run("test.", openAction, valve2);
        verify(runner, never()).run("test.", openAction, valve3);
        verify(runner, never()).run("test.", openAction, valve4);
    }

}
