package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.actions.ValveCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(SetDefaults.class)
@PropertySource("classpath:application-testJobs.properties")
class SetDefaultsTest {

    @MockBean
    private AppState state;

    @MockBean
    private ValveOpenAction openAction;

    @MockBean
    private ValveCloseAction closeAction;

    @Autowired
    private SetDefaults job;

    @Test
    void testJob() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Tank tank = new Tank("tank", 10, null, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, false, RaspiPin.GPIO_01);
        Tank tank2 = new Tank("tank2", 100, null, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        Valve valve3 = new Valve("valve3", "valve3", ValveType.OUTPUT, true, RaspiPin.GPIO_02);
        Valve valve4 = new Valve("valve4", "valve4", ValveType.OUTPUT, false, RaspiPin.GPIO_03);
        when(state.getOutputs()).thenReturn(List.of(valve3, valve4));

        job.run();

        verify(openAction).doAction(valve);
        verify(openAction).doAction(valve3);
        verify(closeAction).doAction(valve2);
        verify(closeAction).doAction(valve4);
    }

}