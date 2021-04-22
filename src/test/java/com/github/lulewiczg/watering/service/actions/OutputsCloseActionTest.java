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
@Import(OutputsCloseAction.class)
@ExtendWith(SpringExtension.class)
class OutputsCloseActionTest {

    @MockBean
    private ValveCloseAction closeAction;

    @MockBean
    private AppState state;

    @MockBean
    private ActionRunner runner;

    @Autowired
    private OutputsCloseAction action;

    @AfterEach
    void after() {
        verifyNoInteractions(closeAction);
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
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        ActionDto actionDto = new ActionDto("test");

        action.doAction(actionDto, null);

        verify(runner).run("test.", closeAction, TestUtils.OUT);
        verify(runner).run("test.", closeAction, TestUtils.OUT2);
        verify(runner, never()).run("test.", closeAction, TestUtils.VALVE);
        verify(runner, never()).run("test.", closeAction, TestUtils.VALVE2);
        verify(runner, never()).run("test.", closeAction, TestUtils.TAP_VALVE);
    }

    @Test
    void testActionNestedFail() {
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.ERROR_RESULT);
        ActionDto actionDto = new ActionDto("test");

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", closeAction, TestUtils.OUT);
        verify(runner, never()).run("test.", closeAction, TestUtils.OUT2);
        verify(runner, never()).run("test.", closeAction, TestUtils.VALVE);
        verify(runner, never()).run("test.", closeAction, TestUtils.VALVE);
        verify(runner, never()).run("test.", closeAction, TestUtils.TAP_VALVE);
    }

}
