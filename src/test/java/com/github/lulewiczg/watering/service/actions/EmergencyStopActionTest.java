package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Import(EmergencyStopAction.class)
@ExtendWith(SpringExtension.class)
class EmergencyStopActionTest {

    @MockitoBean
    private AppState state;

    @MockitoBean
    private TapsCloseAction tapsCloseAction;

    @MockitoBean
    private TanksCloseAction tanksCloseAction;

    @MockitoBean
    private OutputsCloseAction outputsCloseAction;

    @MockitoBean
    private ActionRunner runner;

    @Autowired
    private EmergencyStopAction action;

    @AfterEach
    void after() {
        verifyNoInteractions(tanksCloseAction);
        verifyNoInteractions(tapsCloseAction);
        verifyNoInteractions(outputsCloseAction);
    }

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
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.ERROR_RESULT);

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", tapsCloseAction, null);
        verify(runner).run("test.", outputsCloseAction, null);
    }

    @Test
    void testActionTapsError() {
        ActionDto actionDto = new ActionDto("test");
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsCloseAction, null)).thenReturn(TestUtils.ERROR_RESULT);

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", tapsCloseAction, null);
        verify(runner).run("test.", outputsCloseAction, null);
    }

    @Test
    void testActionOutputsError() {
        ActionDto actionDto = new ActionDto("test");
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tapsCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", outputsCloseAction, null)).thenReturn(TestUtils.ERROR_RESULT);

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, null)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", tapsCloseAction, null);
        verify(runner).run("test.", outputsCloseAction, null);
    }
}
