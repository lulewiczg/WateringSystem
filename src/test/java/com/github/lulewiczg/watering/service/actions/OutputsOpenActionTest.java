package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.github.lulewiczg.watering.state.dto.WaterSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void before() {
        List<Valve> valves = List.of(TestUtils.OUT, TestUtils.OUT2);
        List<Tank> tanks = List.of(TestUtils.TANK, TestUtils.TANK2);
        List<WaterSource> taps = List.of(TestUtils.TAP);
        when(state.getOutputs()).thenReturn(valves);
        when(state.getTanks()).thenReturn(tanks);
        when(state.getTaps()).thenReturn(taps);
    }

    @Test
    void testAction() {
        ActionDto actionDto = new ActionDto("test");
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);

        action.doAction(actionDto, null);

        verify(runner).run("test.", openAction, TestUtils.OUT);
        verify(runner).run("test.", openAction, TestUtils.OUT2);
        verify(runner, never()).run("test.", openAction, TestUtils.VALVE);
        verify(runner, never()).run("test.", openAction, TestUtils.VALVE2);
        verify(runner, never()).run("test.", openAction, TestUtils.TAP_VALVE);
    }

    @Test
    void testActionNestedFail() {
        ActionDto actionDto = new ActionDto("test");
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.ERROR_RESULT);

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", openAction, TestUtils.OUT);
        verify(runner, never()).run("test.", openAction, TestUtils.OUT2);
        verify(runner, never()).run("test.", openAction, TestUtils.VALVE);
        verify(runner, never()).run("test.", openAction, TestUtils.VALVE2);
        verify(runner, never()).run("test.", openAction, TestUtils.TAP_VALVE);

    }

}
