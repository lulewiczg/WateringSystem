package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import(EmergencyStopAction.class)
@ExtendWith(SpringExtension.class)
class EmergencyStopActionTest {

    @MockBean
    private AppState state;

    @MockBean
    private TapsCloseAction tapsCloseAction;

    @MockBean
    private TanksCloseAction tanksCloseAction;

    @MockBean
    private OutputsCloseAction outputsCloseAction;

    @MockBean
    private ActionRunner runner;

    @Autowired
    private EmergencyStopAction action;

    @Test
    void testAction() {
        ActionDto actionDto = new ActionDto("test");
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);

        action.doAction(actionDto, null);

        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", tapsCloseAction, null);
        verify(runner).run("test.", outputsCloseAction, null);
    }

    @Test
    void testActionTanksError() {
        ActionDto actionDto = new ActionDto("test");
        when(runner.run("test.", tapsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenThrow(new ActionException("id", "error"));

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
    }

    @Test
    void testActionTapsError() {
        ActionDto actionDto = new ActionDto("test");
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsCloseAction, null)).thenThrow(new ActionException("id", "error"));

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
    }

    @Test
    void testActionOutputsError() {
        ActionDto actionDto = new ActionDto("test");
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenThrow(new ActionException("id", "error"));

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
    }
}
