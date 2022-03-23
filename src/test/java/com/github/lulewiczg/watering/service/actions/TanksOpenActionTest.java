package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Import(TanksOpenAction.class)
@ExtendWith(SpringExtension.class)
class TanksOpenActionTest {

    @MockBean
    private ValveOpenAction openAction;

    @MockBean
    private PumpStartAction pumpStartAction;

    @MockBean
    private AppState state;

    @MockBean
    private ActionRunner runner;

    @Autowired
    private TanksOpenAction action;

    @AfterEach
    void after() {
        verifyNoInteractions(openAction);
    }

    @BeforeEach
    void before() {
        TestUtils.standardMock(state);
    }

    @Test
    void testAction() {
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run(eq("test."), eq(pumpStartAction), any())).thenReturn(TestUtils.EMPTY_RESULT);

        ActionDto actionDto = new ActionDto("test");

        action.doAction(actionDto, null);

        verify(runner).run("test.", openAction, TestUtils.Objects.VALVE);
        verify(runner).run("test.", openAction, TestUtils.Objects.VALVE2);
        verify(runner).run("test.", pumpStartAction, TestUtils.Objects.PUMP);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.OUT);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.OUT2);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.TAP_VALVE);
    }

    @Test
    void testNestedOpenFail() {
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.ERROR_RESULT);
        when(runner.run(eq("test."), eq(pumpStartAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        ActionDto actionDto = new ActionDto("test");

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", openAction, TestUtils.Objects.VALVE);
        verify(runner, never()).run("test.", pumpStartAction, TestUtils.Objects.PUMP);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.VALVE2);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.OUT);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.OUT);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.TAP_VALVE);
    }

    @Test
    void testNestedPumpStartFail() {
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run(eq("test."), eq(pumpStartAction), any())).thenReturn(TestUtils.ERROR_RESULT);
        ActionDto actionDto = new ActionDto("test");

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", openAction, TestUtils.Objects.VALVE);
        verify(runner).run("test.", pumpStartAction, TestUtils.Objects.PUMP);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.VALVE2);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.OUT);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.OUT);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.TAP_VALVE);
    }
}
