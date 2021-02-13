package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.RaspiPin;
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
        Valve valve = new Valve("test", "test", ValveType.OUTPUT, true, RaspiPin.GPIO_00);

        action.doAction(new ActionDto(), valve);

        verify(service).toggleOff(valve.getPin());
        assertFalse(valve.isOpen());
    }

    @Test
    void testAlreadyClosed() {
        Valve valve = new Valve("test", "test", ValveType.OUTPUT, false, RaspiPin.GPIO_00);

        action.doAction(new ActionDto(), valve);

        verify(service).toggleOff(valve.getPin());
        assertFalse(valve.isOpen());
    }
}
