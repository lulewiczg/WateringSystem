package com.github.lulewiczg.watering.config;

import com.github.lulewiczg.watering.config.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@Import(LocalValidatorFactoryBean.class)
@TestPropertySource(locations = "classpath:application-appConfigTest.properties")
class AppConfigTest {

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    void testPropsNoTank() {
        List<ValveConfig> valves = List.of(new ValveConfig("valve1", "abc", ValveType.INPUT, "GPIO 1", false));
        List<WaterLevelSensorConfig> sensors = List.of(new WaterLevelSensorConfig("sensor1", 12, 21, "GPIO 2"));

        AppConfig config = new AppConfig(List.of(), valves, sensors, validator);

        String message = assertThrows(IllegalStateException.class, config::validate).getMessage();
        assertEquals("No tanks found!", message);
    }

    @Test
    void testPropsNoValve() {
        List<TankConfig> tanks = List.of(new TankConfig("tank", 123, "sensor1", "valve1", TankType.DEFAULT));
        List<WaterLevelSensorConfig> sensors = List.of(new WaterLevelSensorConfig("test", 12, 21, "GPIO 1"));

        AppConfig config = new AppConfig(tanks, List.of(), sensors, validator);

        String message = assertThrows(IllegalStateException.class, config::validate).getMessage();
        assertEquals("No valves found!", message);
    }

    @Test
    void testPropsNoSensor() {
        List<ValveConfig> valves = List.of(new ValveConfig("test", "abc", ValveType.INPUT, "GPIO 1", false));
        List<TankConfig> tanks = List.of(new TankConfig("tank", 123, null, "test", TankType.DEFAULT));

        AppConfig config = new AppConfig(tanks, valves, List.of(), validator);

        config.validate();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/pins-test.csv")
    void testPins(String pin, String pin2, String pin3, String pin4, String error) {
        List<ValveConfig> valves = List.of(new ValveConfig("test", "abc", ValveType.INPUT, pin, false),
                new ValveConfig("test2", "abc2", ValveType.INPUT, pin2, false));
        List<WaterLevelSensorConfig> sensors = List.of(new WaterLevelSensorConfig("test", 1, 2, pin3)
                , new WaterLevelSensorConfig("test2", 1, 2, pin4));
        List<TankConfig> tanks = List.of(new TankConfig("tank", 123, "test", "test", TankType.DEFAULT),
                new TankConfig("tank2", 321, "test2", "test2", TankType.DEFAULT));

        AppConfig config = new AppConfig(tanks, valves, sensors, validator);

        if (error != null) {
            String message = assertThrows(IllegalStateException.class, config::validate).getMessage();
            assertEquals(error, message);
        } else {
            config.validate();
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/sensor-test.csv")
    void testSensor(String id, int min, int max, String error) {
        List<ValveConfig> valves = List.of(new ValveConfig("test", "abc", ValveType.INPUT, "GPIO 1", false));
        List<WaterLevelSensorConfig> sensors = List.of(new WaterLevelSensorConfig(id, min, max, "GPIO 2"));
        List<TankConfig> tanks = List.of(new TankConfig("tank", 123, "sensor", "test", TankType.DEFAULT));

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
    void testValve(String id, String name, ValveType type, boolean open, String error) {
        List<ValveConfig> valves = List.of(new ValveConfig(id, name, type, "GPIO 1", open));
        List<WaterLevelSensorConfig> sensors = List.of(new WaterLevelSensorConfig("test", 1, 2, "GPIO 2"));
        List<TankConfig> tanks = List.of(new TankConfig("tank", 123, "test", "valve", TankType.DEFAULT));

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
    void testTank(String id, Integer volume, String sensorId, String valveId, TankType type, String error) {
        List<ValveConfig> valves = List.of(new ValveConfig("testValve", "test valve", ValveType.INPUT, "GPIO 1", true));
        List<WaterLevelSensorConfig> sensors = List.of(new WaterLevelSensorConfig("testSensor", 1, 2, "GPIO 2"));
        List<TankConfig> tanks = List.of(new TankConfig(id, volume, sensorId, valveId, type));

        AppConfig config = new AppConfig(tanks, valves, sensors, validator);

        if (error != null) {
            String message = assertThrows(IllegalStateException.class, config::validate).getMessage();
            assertEquals(error, message);
        } else {
            config.validate();
        }
    }


}