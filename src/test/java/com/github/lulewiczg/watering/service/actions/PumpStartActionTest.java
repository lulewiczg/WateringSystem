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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@Import(PumpStartAction.class)
@ExtendWith(SpringExtension.class)
class PumpStartActionTest {

    @MockBean
    private IOService service;

    @MockBean
    private AppState state;

    @Autowired
    private PumpStartAction action;

    @Test
    void testClose() {
        action.doAction(new ActionDto(), TestUtils.Objects.PUMP);

        verify(service).toggleOn(TestUtils.Objects.PUMP.getPin());
        assertTrue(TestUtils.Objects.PUMP.isRunning());
    }

    @Test
    void testAlreadyClosed() {
        action.doAction(new ActionDto(), TestUtils.Objects.PUMP);

        verify(service).toggleOn(TestUtils.Objects.PUMP.getPin());
        assertTrue(TestUtils.Objects.PUMP.isRunning());
    }
}
