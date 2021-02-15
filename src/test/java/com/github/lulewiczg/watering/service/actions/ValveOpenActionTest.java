package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Valve valve = new Valve("test", "test", ValveType.OUTPUT, false, RaspiPin.GPIO_00);

        ActionResultDto<Void> result = action.doAction(new ActionDto(), valve);

        verify(service).toggleOn(valve.getPin());
        assertTrue(valve.isOpen());
        TestUtils.testActionResult(result);
    }

    @Test
    void testAlreadyOpened() {
        Valve valve = new Valve("test", "test", ValveType.OUTPUT, true, RaspiPin.GPIO_00);

        ActionResultDto<Void> result = action.doAction(new ActionDto("test"), valve);

        verify(service).toggleOn(valve.getPin());
        assertTrue(valve.isOpen());
        TestUtils.testActionResult(result);
        assertEquals("test", result.getId());
    }
}
