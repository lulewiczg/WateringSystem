package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.exception.HandlerNotFoundException;
import com.github.lulewiczg.watering.exception.ValidationException;
import com.github.lulewiczg.watering.service.actions.dto.WateringDto;
import com.github.lulewiczg.watering.service.actions.dto.WateringDtoMapper;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Pump;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Valve;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import({ActionParamService.class, LocalValidatorFactoryBean.class})
@ExtendWith(SpringExtension.class)
class ActionParamServiceTest {

    @MockBean
    private AppState state;

    @Mock
    private Valve valve;

    @Mock
    private Sensor sensor;

    @Mock
    private Pump pump;

    @Autowired
    private ActionParamService service;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    void testResolveValve() {
        when(state.findValve("id")).thenReturn(valve);
        ActionDefinitionDto def = new ActionDefinitionDto();
        def.setParameterDestinationType(Valve.class);

        Object result = service.mapParam(def, "id");

        assertEquals(valve, result);
    }

    @Test
    void testResolveSensor() {
        when(state.findSensor("id")).thenReturn(sensor);
        ActionDefinitionDto def = new ActionDefinitionDto();
        def.setParameterDestinationType(Sensor.class);

        Object result = service.mapParam(def, "id");

        assertEquals(sensor, result);
    }

    @Test
    void testResolvePump() {
        when(state.findPump("id")).thenReturn(pump);
        ActionDefinitionDto def = new ActionDefinitionDto();
        def.setParameterDestinationType(Pump.class);

        Object result = service.mapParam(def, "id");

        assertEquals(pump, result);
    }

    @Test
    void testResolveWateringDto() {
        ActionDefinitionDto def = new ActionDefinitionDto();
        def.setParameterDestinationType(WateringDto.class);
        Map<String, Object> map = Map.of("valveId", "id", "seconds", 1);
        WateringDto dto = new WateringDto("id", null, 1, null);
        Object result = service.mapParam(def, map);

        assertEquals(dto, result);
    }

    @Test
    void testResolveWateringMissingId() {
        ActionDefinitionDto def = new ActionDefinitionDto();
        def.setParameterDestinationType(WateringDto.class);
        Map<String, Object> map = Map.of("seconds", 1);
        WateringDto dto = new WateringDto(null, null, 1, null);

        String message = assertThrows(ValidationException.class, () -> service.mapParam(def, map)).getMessage();

        assertEquals("valveId must not be empty", message);
    }

    @Test
    void testResolveWateringMissingSeconds() {
        ActionDefinitionDto def = new ActionDefinitionDto();
        def.setParameterDestinationType(WateringDto.class);
        Map<String, Object> map = Map.of("valveId", "id");
        WateringDto dto = new WateringDto("id", null, null, null);

        String message = assertThrows(ValidationException.class, () -> service.mapParam(def, map)).getMessage();

        assertEquals("seconds must not be null", message);
    }

    @Test
    void testResolveOther() {
        ActionDefinitionDto def = new ActionDefinitionDto();
        def.setParameterDestinationType(Object.class);

        String message = assertThrows(HandlerNotFoundException.class, () -> service.mapParam(def, "id")).getMessage();

        assertEquals("No handler found to handle [id] as [class java.lang.Object]!", message);
    }
}