package com.github.lulewiczg.watering.config;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.dto.*;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Import(LocalValidatorFactoryBean.class)
class AppConfigTest {

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    void testPropsNoTank() {
        List<ValveConfig> valves = List.of(TestUtils.Config.VALVE);
        List<WaterLevelSensorConfig> sensors = List.of(TestUtils.Config.SENSOR);

        AppConfig config = new AppConfig(List.of(), valves, sensors, null);

        testValidate(config, "tanks must not be empty");
    }

    @Test
    void testPropsNoValve() {
        List<TankConfig> tanks = List.of(TestUtils.Config.TANK);
        List<WaterLevelSensorConfig> sensors = List.of(TestUtils.Config.SENSOR);

        AppConfig config = new AppConfig(tanks, List.of(), sensors, null);

        testValidate(config, "valves must not be empty");
    }

    @Test
    void testPropsNoSensor() {
        List<TankConfig> tanks = List.of(TestUtils.Config.TANK_NO_SENSOR);
        List<ValveConfig> valves = List.of(TestUtils.Config.VALVE);

        AppConfig config = new AppConfig(tanks, valves, List.of(), null);

        testValidate(config, null);
    }

    @Test
    void testPumpNoMapping() {
        List<TankConfig> tanks = List.of(TestUtils.Config.TANK_NO_SENSOR);
        List<ValveConfig> valves = List.of(TestUtils.Config.VALVE);

        AppConfig config = new AppConfig(tanks, valves, List.of(), null);

        testValidate(config, null);
    }

    @Test
    void testNoPumps() {
        List<TankConfig> tanks = List.of(TestUtils.Config.TANK);
        List<ValveConfig> valves = List.of(TestUtils.Config.VALVE);
        List<WaterLevelSensorConfig> sensors = List.of(TestUtils.Config.SENSOR);


        AppConfig config = new AppConfig(tanks, valves, sensors, null);

        testValidate(config, "No pumps declared!");
    }

    @Test
    void testDanglingPump() {
        List<TankConfig> tanks = List.of(TestUtils.Config.TANK_NO_SENSOR);
        List<ValveConfig> valves = List.of(TestUtils.Config.VALVE);
        List<PumpConfig> pumps = List.of(TestUtils.Config.PUMP);

        AppConfig config = new AppConfig(tanks, valves, List.of(), pumps);

        testValidate(config, "Pump pump1 has no mapping!");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/pins-test.csv")
    void testPins(String pin, String pin2, Address address, Address address2, String powerPin, String powerPin2, String pumpPin, String error) {
        ValveConfig valve = new ValveConfig("valve1", "abc", ValveType.INPUT, pin, false, false, null);
        ValveConfig valve2 = new ValveConfig("valve2", "abc2", ValveType.INPUT, pin2, false, false, null);
        List<ValveConfig> valves = List.of(valve, valve2);
        List<TankConfig> tanks = List.of(TestUtils.Config.TANK, TestUtils.Config.TANK2);
        WaterLevelSensorConfig sensor = new WaterLevelSensorConfig("sensor1", 1, 2, address, powerPin, 10, 100, 200, 12);
        WaterLevelSensorConfig sensor2 = new WaterLevelSensorConfig("sensor2", 1, 2, address2, powerPin2, 10, 100, 200, 12);
        List<WaterLevelSensorConfig> sensors = List.of(sensor, sensor2);
        PumpConfig pump = new PumpConfig("pump1", "pump1", pumpPin);
        List<PumpConfig> pumps = List.of(pump);

        AppConfig config = new AppConfig(tanks, valves, sensors, pumps);

        testValidate(config, error);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/sensor-test.csv")
    void testSensor(String id, int min, int max, int resistorsNumber, int passiveResistance, int stepResistance, double voltage, String error) {
        List<ValveConfig> valves = List.of(TestUtils.Config.VALVE);
        WaterLevelSensorConfig sensor = new WaterLevelSensorConfig(id, min, max, Address.ADDR_41, "GPIO 10", resistorsNumber, passiveResistance, stepResistance, voltage);
        List<WaterLevelSensorConfig> sensors = List.of(sensor);
        List<TankConfig> tanks = List.of(TestUtils.Config.TANK);
        List<PumpConfig> pumps = List.of(TestUtils.Config.PUMP);

        AppConfig config = new AppConfig(tanks, valves, sensors, pumps);

        testValidate(config, error);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/valve-test.csv")
    void testValve(String id, String name, ValveType type, boolean open, Long wateringTime, String error) {
        List<ValveConfig> valves = List.of(new ValveConfig(id, name, type, "GPIO 1", open, false, wateringTime));
        List<WaterLevelSensorConfig> sensors = List.of(TestUtils.Config.SENSOR);
        List<TankConfig> tanks = List.of(TestUtils.Config.TANK);
        List<PumpConfig> pumps = List.of(TestUtils.Config.PUMP);

        AppConfig config = new AppConfig(tanks, valves, sensors, pumps);

        testValidate(config, error);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/tank-test.csv")
    void testTank(String id, Integer volume, String sensorId, String valveId, String pumpId, TankType type, String error) {
        List<ValveConfig> valves = List.of(new ValveConfig("testValve", "test valve", ValveType.INPUT, "GPIO 1", true, false, null));
        List<WaterLevelSensorConfig> sensors = List.of(new WaterLevelSensorConfig("testSensor", 1, 2, Address.ADDR_41, "GPIO 10", 10, 100, 200, 12));
        List<TankConfig> tanks = List.of(new TankConfig(id, volume, sensorId, valveId, pumpId, type));
        List<PumpConfig> pumps = List.of((TestUtils.Config.PUMP));

        AppConfig config = new AppConfig(tanks, valves, sensors, pumps);

        testValidate(config, error);
    }

    private void testValidate(AppConfig config, String message) {
        ConstraintViolation<AppConfig> error = validateFields(config);
        if (error != null) {
            assertEquals(message, error.getPropertyPath() + " " + error.getMessage());
        } else {
            if (message != null) {
                String msg = assertThrows(IllegalStateException.class, config::validate).getMessage();
                assertEquals(message, msg);
            } else {
                assertDoesNotThrow(config::validate);
            }
        }
    }

    private ConstraintViolation<AppConfig> validateFields(AppConfig config) {
        Set<ConstraintViolation<AppConfig>> errors = validator.validate(config);
        Optional<ConstraintViolation<AppConfig>> error = errors.stream().min(Comparator.comparing(i -> i.getPropertyPath().toString()));
        return error.orElse(null);
    }

}
