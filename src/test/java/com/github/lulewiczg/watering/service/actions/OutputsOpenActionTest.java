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
        TestUtils.standardMock(state);
    }

    @Test
    void testAction() {
        ActionDto actionDto = new ActionDto("test");
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);

        action.doAction(actionDto, null);

        verify(runner).run("test.", openAction, TestUtils.Objects.OUT);
        verify(runner).run("test.", openAction, TestUtils.Objects.OUT2);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.VALVE);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.VALVE2);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.TAP_VALVE);
    }

    @Test
    void testActionNestedFail() {
        ActionDto actionDto = new ActionDto("test");
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.ERROR_RESULT);

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", openAction, TestUtils.Objects.OUT);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.OUT2);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.VALVE);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.VALVE2);
        verify(runner, never()).run("test.", openAction, TestUtils.Objects.TAP_VALVE);

    }

}
