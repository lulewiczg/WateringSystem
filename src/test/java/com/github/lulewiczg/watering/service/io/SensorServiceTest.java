package com.github.lulewiczg.watering.service.io;

import com.github.lulewiczg.watering.service.ina219.enums.Address;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@Import(SensorService.class)
@ExtendWith(SpringExtension.class)
class SensorServiceTest {

    @Autowired
    private SensorService service;

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/levels-test.csv")
    void testLevelCalculation(double current, int resistorsNumber, int passiveResistance, int stepResistance, double voltage, double expected) {
        Sensor sensor = new Sensor("sensor", 0, 1, null, Address.ADDR_40, RaspiPin.GPIO_10, resistorsNumber, passiveResistance, stepResistance, voltage);

        double result = service.calculateWaterLevel(current, sensor);

        assertEquals(expected, result, 0.00001);
    }

}