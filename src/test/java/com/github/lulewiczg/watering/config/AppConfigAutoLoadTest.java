package com.github.lulewiczg.watering.config;

import com.github.lulewiczg.watering.config.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AppConfigAutoLoadTest {

    @Autowired
    private AppConfig config;

    @Test
    void testPropsLoad() {
        Map<String, ValveConfig> valves = new HashMap<>();
        ValveConfig valve = new ValveConfig("Tank 1", ValveType.INPUT, "GPIO 3", false);
        ValveConfig valve2 = new ValveConfig("Tank 2", ValveType.INPUT, "GPIO 4", false);
        ValveConfig valve3 = new ValveConfig("tap water", ValveType.INPUT, "GPIO 5", false);
        ValveConfig valve4 = new ValveConfig("out", ValveType.OUTPUT, "GPIO 6", true);
        valves.put("valve1", valve);
        valves.put("valve2", valve2);
        valves.put("tap", valve3);
        valves.put("garden", valve4);
        assertEquals(valves, config.getValves());

        Map<String, WaterLevelSensorConfig> sensors = new HashMap<>();
        WaterLevelSensorConfig sensor = new WaterLevelSensorConfig(12, 21, "GPIO 1");
        WaterLevelSensorConfig sensor2 = new WaterLevelSensorConfig(99, 100, "GPIO 2");
        sensors.put("sensor1", sensor);
        sensors.put("sensor2", sensor2);
        assertEquals(sensors, config.getSensors());

        Map<String, TankConfig> tanks = new HashMap<>();
        tanks.put("tank1", new TankConfig(123, "sensor1", "valve1", TankType.DEFAULT));
        tanks.put("tank2", new TankConfig(321, "sensor2", "valve2", TankType.DEFAULT));
        tanks.put("tap", new TankConfig(null, null, "tap", TankType.UNLIMITED));
        Map<String, TankConfig> configuredTanks = config.getTanks();
        assertEquals(tanks, configuredTanks);

        assertEquals(configuredTanks.get("tank1").getSensor(), sensor);
        assertEquals(configuredTanks.get("tank2").getSensor(), sensor2);
        assertNull(configuredTanks.get("tap").getSensor());
        assertEquals(configuredTanks.get("tank1").getValve(), valve);
        assertEquals(configuredTanks.get("tank2").getValve(), valve2);
        assertEquals(configuredTanks.get("tap").getValve(), valve3);
    }

}