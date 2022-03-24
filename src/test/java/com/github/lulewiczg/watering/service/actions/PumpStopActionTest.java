package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import(PumpStopAction.class)
@ExtendWith(SpringExtension.class)
class PumpStopActionTest {

    @MockBean
    private IOService service;

    @MockBean
    private AppState state;

    @Autowired
    private PumpStopAction action;

    @Test
    void testClose() {
        action.doAction(new ActionDto(), TestUtils.Objects.PUMP);

        verify(service).toggleOff(TestUtils.Objects.PUMP.getPin());
        assertFalse(TestUtils.Objects.PUMP.isRunning());
    }

    @Test
    void testAlreadyClosed() {
        action.doAction(new ActionDto(), TestUtils.Objects.PUMP);

        verify(service).toggleOff(TestUtils.Objects.PUMP.getPin());
        assertFalse(TestUtils.Objects.PUMP.isRunning());
    }


    @Test
    void testActionEnabled() {
        when(state.getPumps()).thenReturn(List.of(TestUtils.Objects.PUMP));

        assertTrue(action.isEnabled());
    }

    @Test
    void testActionDisabled() {
        assertFalse(action.isEnabled());
    }

    @Test
    void testActionDisabledEmptyList() {
        when(state.getPumps()).thenReturn(List.of());
        assertFalse(action.isEnabled());
    }
}
