package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.state.Tank;
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

    @Test
    void testStateCreation() {
        assertEquals(1, state.getOutputValves().size());
        assertEquals(config.getValves().get("garden"), state.getOutputValves().get(0).getConfig());

        assertEquals(3, state.getTanks().size());
        testTank(config.getTanks().get("tank1"), state.getTanks().get(0));
        testTank(config.getTanks().get("tap"), state.getTanks().get(1));
        testTank(config.getTanks().get("tank2"), state.getTanks().get(2));


    }

    private void testTank(TankConfig tankConfig, Tank tank) {
        assertEquals(tankConfig, tank.getConfig());
        assertEquals(tankConfig.getSensor(), tank.getSensor().getConfig());
        assertEquals(tankConfig.getValve(), tank.getValve().getConfig());
    }
}