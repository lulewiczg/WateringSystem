package com.github.lulewiczg.watering.config;

import com.github.lulewiczg.watering.config.dto.*;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AppConfigAutoLoadTest {

    @Autowired
    private AppConfig config;

    @Test
    void testPropsLoad() {
        ValveConfig valve = new ValveConfig("valve1", "Tank 1", ValveType.INPUT, "GPIO 3", false,false,null);
        ValveConfig valve2 = new ValveConfig("valve2", "Tank 2", ValveType.INPUT, "GPIO 4", false,false,null);
        ValveConfig valve3 = new ValveConfig("tap", "tap water", ValveType.INPUT, "GPIO 5", false,false,null);
        ValveConfig valve4 = new ValveConfig("out", "out", ValveType.OUTPUT, "GPIO 6", true,true, 333L);
        List<ValveConfig> valves = List.of(valve, valve2, valve3, valve4);
        assertEquals(valves, config.getValves());

        WaterLevelSensorConfig sensor = new WaterLevelSensorConfig("sensor1", 12, 21, Address.ADDR_40, "GPIO 10", 10, 1000, 100, 12);
        WaterLevelSensorConfig sensor2 = new WaterLevelSensorConfig("sensor2", 5, 100, Address.ADDR_41, "", 20, 50, 60, 5);
        List<WaterLevelSensorConfig> sensors = List.of(sensor, sensor2);
        assertEquals(sensors, config.getSensors());

        List<TankConfig> tanks = new ArrayList<>();
        tanks.add(new TankConfig("tank1", 123, "sensor1", "valve1", TankType.DEFAULT));
        tanks.add(new TankConfig("tank2", 321, "sensor2", "valve2", TankType.DEFAULT));
        tanks.add(new TankConfig("tap", null, null, "tap", TankType.UNLIMITED));
        List<TankConfig> configuredTanks = config.getTanks();
        assertEquals(tanks, configuredTanks);

        assertEquals(configuredTanks.get(0).getSensor(), sensor);
        assertEquals(configuredTanks.get(1).getSensor(), sensor2);
        assertNull(configuredTanks.get(2).getSensor());
        assertEquals(configuredTanks.get(0).getValve(), valve);
        assertEquals(configuredTanks.get(1).getValve(), valve2);
        assertEquals(configuredTanks.get(2).getValve(), valve3);
    }

}
