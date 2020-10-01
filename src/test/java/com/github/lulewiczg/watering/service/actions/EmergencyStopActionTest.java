package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.config.dto.TankType;
import com.github.lulewiczg.watering.config.dto.ValveConfig;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.AppState;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.Tank;
import com.github.lulewiczg.watering.state.Valve;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import(EmergencyStopAction.class)
@ExtendWith(SpringExtension.class)
class EmergencyStopActionTest {

    @MockBean
    private IOService ioService;

    @MockBean
    private AppState state;

    @Autowired
    private EmergencyStopAction action;

    @Test
    void testAction() {
        List<Valve> valves = List.of(new Valve(new ValveConfig("test", ValveType.OUTPUT, "", true, RaspiPin.GPIO_00)),
                new Valve(new ValveConfig("test", ValveType.OUTPUT, "", true, RaspiPin.GPIO_01)));
        List<Tank> tanks = List.of(new Tank(new TankConfig(1, null, null, TankType.DEFAULT, null, new ValveConfig("test", ValveType.OUTPUT, "", true, RaspiPin.GPIO_03))),
                new Tank(new TankConfig(1, null, null, TankType.DEFAULT, null, new ValveConfig("test", ValveType.OUTPUT, "", true, RaspiPin.GPIO_04))));
        when(state.getOutputValves()).thenReturn(valves);
        when(state.getTanks()).thenReturn(tanks);

        action.doAction(null);

        verify(ioService).toggleOff(RaspiPin.GPIO_00);
        verify(ioService).toggleOff(RaspiPin.GPIO_01);
        verify(ioService).toggleOff(RaspiPin.GPIO_03);
        verify(ioService).toggleOff(RaspiPin.GPIO_04);
    }

}