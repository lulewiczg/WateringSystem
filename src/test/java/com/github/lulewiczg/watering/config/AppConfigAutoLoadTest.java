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

@SpringBootTest()
@TestPropertySource(locations = "classpath:application-test.properties")
class AppConfigAutoLoadTest {

    @Autowired
    private AppConfig config;

    @Test
    void testPropsLoad() {
        Map<String, Valve> valves = new HashMap<>();
        Valve valve = new Valve("Tank 1", ValveType.INPUT, false);
        Valve valve2 = new Valve("Tank 2", ValveType.INPUT, false);
        Valve valve3 = new Valve("tap water", ValveType.INPUT, false);
        Valve valve4 = new Valve("out", ValveType.OUTPUT, true);
        valves.put("valve1", valve);
        valves.put("valve2", valve2);
        valves.put("tap", valve3);
        valves.put("garden", valve4);
        assertEquals(valves, config.getValves());

        Map<String, WaterLevelSensor> sensors = new HashMap<>();
        WaterLevelSensor sensor = new WaterLevelSensor(12, 21);
        WaterLevelSensor sensor2 = new WaterLevelSensor(99, 100);
        sensors.put("sensor1", sensor);
        sensors.put("sensor2", sensor2);
        assertEquals(sensors, config.getSensors());

        Map<String, Tank> tanks = new HashMap<>();
        tanks.put("tank1", new Tank(123, "sensor1", "valve1", TankType.DEFAULT));
        tanks.put("tank2", new Tank(321, "sensor2", "valve2", TankType.DEFAULT));
        tanks.put("tap", new Tank(null, null, "tap", TankType.UNLIMITED));
        Map<String, Tank> configuredTanks = config.getTanks();
        assertEquals(tanks, configuredTanks);

        assertEquals(configuredTanks.get("tank1").getSensor(), sensor);
        assertEquals(configuredTanks.get("tank2").getSensor(), sensor2);
        assertNull(configuredTanks.get("tap").getSensor());
        assertEquals(configuredTanks.get("tank1").getValve(), valve);
        assertEquals(configuredTanks.get("tank2").getValve(), valve2);
        assertEquals(configuredTanks.get("tap").getValve(), valve3);
    }

}