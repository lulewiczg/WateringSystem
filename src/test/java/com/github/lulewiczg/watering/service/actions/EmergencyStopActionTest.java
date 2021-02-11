package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.state.AppState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;

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

    @Autowired
    private EmergencyStopAction action;

    @Test
    void testAction() {
        action.doAction(null);

        verify(tanksCloseAction).doAction(null);
        verify(tapsCloseAction).doAction(null);
        verify(outputsCloseAction).doAction(null);
    }

}
