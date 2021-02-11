package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.exception.SensorNotFoundException;
import com.github.lulewiczg.watering.exception.ValveNotFoundException;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.github.lulewiczg.watering.state.mapper.TankMapper;
import com.github.lulewiczg.watering.state.mapper.ValveMapper;
import com.github.lulewiczg.watering.state.mapper.WaterSourceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AppStateTest {

    @Autowired
    private AppConfig config;

    @Autowired
    private AppState state;

    @Autowired
    private TankMapper tankMapper;

    @Autowired
    private ValveMapper valveMapper;

    @Autowired
    private WaterSourceMapper waterSourceMapper;

    @Test
    void testStateCreation() {
        assertEquals(1, state.getOutputs().size());
        assertEquals(valveMapper.map(config.getValves().get(3)), state.getOutputs().get(0));
        assertEquals(2, state.getTanks().size());
        assertEquals(1, state.getTaps().size());

        assertEquals(tankMapper.map(config.getTanks().get(0)), state.getTanks().get(0));
        assertEquals(tankMapper.map(config.getTanks().get(1)), state.getTanks().get(1));
        assertEquals(waterSourceMapper.map(config.getTanks().get(2)), state.getTaps().get(0));
    }

    @Test
    void testFindSensor() {
        Sensor sensor = state.findSensor("sensor2");

        assertEquals(state.getTanks().get(1).getSensor(), sensor);
    }

    @Test
    void testSensorNotFound() {
        assertThrows(SensorNotFoundException.class, () -> state.findSensor("qweretyuiop"));
    }

    @Test
    void testFindValve() {
        Valve valve = state.findValve("valve2");
        assertEquals(state.getTanks().get(1).getValve(), valve);

        Valve valve2 = state.findValve("tap");
        assertEquals(state.getTaps().get(0).getValve(), valve2);

        Valve valve3 = state.findValve("out");
        assertEquals(state.getOutputs().get(0), valve3);
    }

    @Test
    void tetFindValveNotFound() {
        assertThrows(ValveNotFoundException.class, () -> state.findValve("qweretyuiop"));
    }

}
