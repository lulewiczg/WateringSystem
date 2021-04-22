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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Import(TapsCloseAction.class)
@ExtendWith(SpringExtension.class)
class TapsCloseActionTest {

    @MockBean
    private ValveCloseAction closeAction;

    @MockBean
    private AppState state;

    @MockBean
    private ActionRunner runner;

    @Autowired
    private TapsCloseAction action;

    @AfterEach
    void after() {
        verifyNoInteractions(closeAction);
    }

    @BeforeEach
    void before() {
        TestUtils.standardMock(state);
    }

    @Test
    void testAction() {
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        ActionDto actionDto = new ActionDto("test");

        action.doAction(actionDto, null);

        verify(runner).run("test.", closeAction, TestUtils.Objects.TAP_VALVE);
        verify(runner, never()).run("test.", closeAction, TestUtils.Objects.VALVE);
        verify(runner, never()).run("test.", closeAction, TestUtils.Objects.VALVE);
        verify(runner, never()).run("test.", closeAction, TestUtils.Objects.OUT);
        verify(runner, never()).run("test.", closeAction, TestUtils.Objects.OUT2);
    }

    @Test
    void testActionNestedFail() {
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.ERROR_RESULT);
        ActionDto actionDto = new ActionDto("test");

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", closeAction, TestUtils.Objects.TAP_VALVE);
        verify(runner, never()).run("test.", closeAction, TestUtils.Objects.VALVE);
        verify(runner, never()).run("test.", closeAction, TestUtils.Objects.VALVE);
        verify(runner, never()).run("test.", closeAction, TestUtils.Objects.OUT);
        verify(runner, never()).run("test.", closeAction, TestUtils.Objects.OUT2);
    }

    @Test
    void testActionEnabled() {
        assertTrue(action.isEnabled());
    }

    @Test
    void testActionDisabled() {
        when(state.getTaps()).thenReturn(List.of());

        assertFalse(action.isEnabled());
    }

}
