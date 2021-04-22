package com.github.lulewiczg.watering.config;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.config.dto.ValveConfig;
import com.github.lulewiczg.watering.config.dto.WaterLevelSensorConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

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
        List<ValveConfig> valves = List.of(TestUtils.Config.VALVE, TestUtils.Config.VALVE2, TestUtils.Config.VALVE3, TestUtils.Config.OUT);
        assertEquals(valves, config.getValves());

        List<WaterLevelSensorConfig> sensors = List.of(TestUtils.Config.SENSOR, TestUtils.Config.SENSOR2);
        assertEquals(sensors, config.getSensors());

        List<TankConfig> tanks = List.of(TestUtils.Config.TANK, TestUtils.Config.TANK2, TestUtils.Config.TAP);
        List<TankConfig> configuredTanks = config.getTanks();
        assertEquals(tanks, configuredTanks);

        assertEquals(TestUtils.Config.SENSOR, configuredTanks.get(0).getSensor());
        assertEquals(TestUtils.Config.SENSOR2, configuredTanks.get(1).getSensor());
        assertNull(configuredTanks.get(2).getSensor());
        assertEquals(TestUtils.Config.VALVE, configuredTanks.get(0).getValve());
        assertEquals(TestUtils.Config.VALVE2, configuredTanks.get(1).getValve());
        assertEquals(TestUtils.Config.VALVE3, configuredTanks.get(2).getValve());
    }

}
