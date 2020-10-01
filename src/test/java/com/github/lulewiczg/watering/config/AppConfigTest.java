package com.github.lulewiczg.watering.config;

import com.github.lulewiczg.watering.config.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest()
@Import(LocalValidatorFactoryBean.class)
@TestPropertySource(locations = "classpath:application-appConfigTest.properties")
class AppConfigTest {

    @Autowired
    private LocalValidatorFactoryBean validator;


    @Test
    void testPropsNoTank() {
        Map<String, ValveConfig> valves = Map.of("test", new ValveConfig("abc", ValveType.INPUT, false));
        Map<String, WaterLevelSensorConfig> sensors = Map.of("test", new WaterLevelSensorConfig(12, 21));

        AppConfig config = new AppConfig(Map.of(), valves, sensors, validator);

        String message = assertThrows(IllegalStateException.class, config::validate).getMessage();
        assertEquals("No tanks found!", message);
    }


    @Test
    void testPropsNoValve() {
        Map<String, TankConfig> tanks = Map.of("tank", new TankConfig(123, "sensor1", "valve1", TankType.DEFAULT));
        Map<String, WaterLevelSensorConfig> sensors = Map.of("test", new WaterLevelSensorConfig(12, 21));

        AppConfig config = new AppConfig(tanks, Map.of(), sensors, validator);

        String message = assertThrows(IllegalStateException.class, config::validate).getMessage();
        assertEquals("No valves found!", message);
    }

    @Test
    void testPropsNoSensor() {
        Map<String, ValveConfig> valves = Map.of("test", new ValveConfig("abc", ValveType.INPUT, false));
        Map<String, TankConfig> tanks = Map.of("tank", new TankConfig(123, null, "test", TankType.DEFAULT));

        AppConfig config = new AppConfig(tanks, valves, Map.of(), validator);

        config.validate();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/sensor-test.csv")
    void testSensor(int min, int max, String error) {
        Map<String, ValveConfig> valves = Map.of("test", new ValveConfig("abc", ValveType.INPUT, false));
        Map<String, WaterLevelSensorConfig> sensors = Map.of("test", new WaterLevelSensorConfig(min, max));
        Map<String, TankConfig> tanks = Map.of("tank", new TankConfig(123, "test", "test", TankType.DEFAULT));

        AppConfig config = new AppConfig(tanks, valves, sensors, validator);

        if (error != null) {
            String message = assertThrows(IllegalStateException.class, config::validate).getMessage();
            assertEquals(error, message);
        } else {
            config.validate();
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/valve-test.csv")
    void testValve(String name, ValveType type, boolean open, String error) {
        Map<String, ValveConfig> valves = Map.of("test", new ValveConfig(name, type, open));
        Map<String, WaterLevelSensorConfig> sensors = Map.of("test", new WaterLevelSensorConfig(1, 2));
        Map<String, TankConfig> tanks = Map.of("tank", new TankConfig(123, "test", "test", TankType.DEFAULT));

        AppConfig config = new AppConfig(tanks, valves, sensors, validator);

        if (error != null) {
            String message = assertThrows(IllegalStateException.class, config::validate).getMessage();
            assertEquals(error, message);
        } else {
            config.validate();
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/tank-test.csv")
    void testTank(Integer volume, String sensorId, String valveId, TankType type, String error) {
        Map<String, ValveConfig> valves = Map.of("testValve", new ValveConfig("test valve", ValveType.INPUT, true));
        Map<String, WaterLevelSensorConfig> sensors = Map.of("testSensor", new WaterLevelSensorConfig(1, 2));
        Map<String, TankConfig> tanks = Map.of("tank", new TankConfig(volume, sensorId, valveId, type));

        AppConfig config = new AppConfig(tanks, valves, sensors, validator);

        if (error != null) {
            String message = assertThrows(IllegalStateException.class, config::validate).getMessage();
            assertEquals(error, message);
        } else {
            config.validate();
        }
    }


}