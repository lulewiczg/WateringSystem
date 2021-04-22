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
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@Import(ValveCloseAction.class)
@ExtendWith(SpringExtension.class)
class ValveCloseActionTest {

    @MockBean
    private IOService service;

    @MockBean
    private AppState state;

    @Autowired
    private ValveCloseAction action;

    @Test
    void testClose() {
        action.doAction(new ActionDto(), TestUtils.VALVE);

        verify(service).toggleOff(TestUtils.VALVE.getPin());
    }

    @Test
    void testAlreadyClosed() {
        action.doAction(new ActionDto(), TestUtils.VALVE);

        verify(service).toggleOff(TestUtils.VALVE.getPin());
        assertFalse(TestUtils.VALVE.isOpen());
    }
}
