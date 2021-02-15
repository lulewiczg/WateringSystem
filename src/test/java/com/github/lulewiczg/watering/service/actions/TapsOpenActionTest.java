package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        when(openAction.doAction(argThat(i -> i.getId() != null), any())).thenCallRealMethod();
        ActionDto actionDto = new ActionDto();

        ActionResultDto<Void> result = action.doAction(actionDto, null);

        verify(openAction).doAction(argThat(i -> i.getId() != null), eq(valve4));
        verify(openAction, never()).doAction(argThat(i -> i.getId() != null), eq(valve));
        verify(openAction, never()).doAction(argThat(i -> i.getId() != null), eq(valve2));
        verify(openAction, never()).doAction(argThat(i -> i.getId() != null), eq(valve3));
        TestUtils.testActionResult(result);
        assertNull(actionDto.getId());
    }

    @Test
    void testActionWithId() {
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
        ActionDto actionDto = new ActionDto("test");
        ActionDto nestedDto = new ActionDto("test.");
        when(openAction.doAction(nestedDto, null)).thenCallRealMethod();

        ActionResultDto<Void> result = action.doAction(actionDto, null);

        verify(openAction).doAction(nestedDto, valve4);
        verify(openAction, never()).doAction(nestedDto, valve);
        verify(openAction, never()).doAction(nestedDto, valve2);
        verify(openAction, never()).doAction(nestedDto, valve3);
        TestUtils.testActionResult(result);
        assertEquals("test", actionDto.getId());
        assertEquals("test", result.getId());
    }
    @Test
    void testActionEnabled() {
        when(state.getTaps()).thenReturn(List.of(new WaterSource()));

        assertTrue(action.isEnabled());
    }

    @Test
    void testActionDisabled() {
        assertFalse(action.isEnabled());
    }

}
