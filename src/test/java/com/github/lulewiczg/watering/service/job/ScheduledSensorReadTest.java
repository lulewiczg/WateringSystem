package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.service.actions.WaterLevelReadAction;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(ScheduledSensorRead.class)
@PropertySource("classpath:application-testJobs.properties")
class ScheduledSensorReadTest {

    @MockBean
    private AppState state;

    @MockBean
    private WaterLevelReadAction readAction;

    @Autowired
    private ScheduledSensorRead job;

    @Test
    void testJob() {
        Valve valve = new Valve("valve", "valve", ValveType.OUTPUT, true, RaspiPin.GPIO_00);
        Sensor sensor = new Sensor("sensor", 10, 90, null, RaspiPin.GPIO_01);
        Tank tank = new Tank("tank", 100, sensor, valve);
        Valve valve2 = new Valve("valve2", "valve2", ValveType.OUTPUT, true, RaspiPin.GPIO_01);
        Sensor sensor2 = new Sensor("sensor2", 10, 90, 10, RaspiPin.GPIO_02);
        Tank tank2 = new Tank("tank2", 100, sensor2, valve2);
        when(state.getTanks()).thenReturn(List.of(tank, tank2));
        when(readAction.doAction(sensor)).thenReturn(11.0);
        when(readAction.doAction(sensor2)).thenReturn(22.0);

        job.run();

        verify(readAction).doAction(sensor);
        verify(readAction).doAction(sensor2);
        assertEquals(11, sensor.getLevel());
        assertEquals(22, sensor2.getLevel());
    }

}