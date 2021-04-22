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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@Import(ValveOpenAction.class)
@ExtendWith(SpringExtension.class)
class ValveOpenActionTest {

    @MockBean
    private IOService service;

    @MockBean
    private AppState state;

    @Autowired
    private ValveOpenAction action;

    @Test
    void testOpen() {
        action.doAction(new ActionDto(), TestUtils.Objects.VALVE);

        verify(service).toggleOn(TestUtils.Objects.VALVE.getPin());
        assertTrue(TestUtils.Objects.VALVE.isOpen());
    }

    @Test
    void testAlreadyOpened() {
        action.doAction(new ActionDto(), TestUtils.Objects.VALVE);

        verify(service).toggleOn(TestUtils.Objects.VALVE.getPin());
        assertTrue(TestUtils.Objects.VALVE.isOpen());
    }
}
