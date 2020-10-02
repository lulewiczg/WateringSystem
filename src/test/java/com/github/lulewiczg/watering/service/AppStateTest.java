package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.mapper.TankConfigToTankMapper;
import com.github.lulewiczg.watering.state.mapper.ValveConfigToValveMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AppStateTest {

    @Autowired
    private AppConfig config;

    @Autowired
    private AppState state;

    @Autowired
    private TankConfigToTankMapper tankMapper;

    @Autowired
    private ValveConfigToValveMapper valveMapper;

    @Test
    void testStateCreation() {
        assertEquals(1, state.getOutputs().size());
        assertEquals(valveMapper.map(config.getValves().get("garden")), state.getOutputs().get(0));
        assertEquals(2, state.getTanks().size());
        assertEquals(1, state.getTaps().size());

        testTank(config.getTanks().get("tank1"), state.getTanks().get(0));
        testTank(config.getTanks().get("tap"), state.getTaps().get(0));
        testTank(config.getTanks().get("tank2"), state.getTanks().get(1));
    }

    private void testTank(TankConfig tankConfig, Tank tank) {
        assertEquals(tankMapper.map(tankConfig), tank);
    }
}