package com.github.lulewiczg.watering.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.exception.HandlerNotFoundException;
import com.github.lulewiczg.watering.exception.ValidationException;
import com.github.lulewiczg.watering.service.actions.dto.WateringDto;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Pump;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Service for action params logic.
 */
@Service
@RequiredArgsConstructor
public class ActionParamService {

    @Getter(AccessLevel.PRIVATE)
    private final AppState state;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final LocalValidatorFactoryBean validator;

    private final Map<Class<?>, Function<Object, Object>> handlers = Map.of(
            Valve.class, i -> getState().findValve(i.toString()),
            Sensor.class, i -> getState().findSensor(i.toString()),
            Pump.class, i -> getState().findPump(i.toString()),
            Void.class, i -> null,
            WateringDto.class, this::handleWateringDto);

    /**
     * Resolves action param
     *
     * @param definition action definition
     * @param value      value
     * @return resolved param
     */
    @SneakyThrows
    public Object mapParam(ActionDefinitionDto definition, Object value) {
        Class<?> destType = definition.getParameterDestinationType();
        Function<Object, Object> handler = handlers.get(destType);
        if (handler == null) {
            throw new HandlerNotFoundException(value, definition.getParameterDestinationType());
        }
        return handler.apply(value);
    }

    private WateringDto handleWateringDto(Object value) {
        WateringDto wateringDto = MAPPER.convertValue(value, WateringDto.class);
        Set<ConstraintViolation<WateringDto>> errors = validator.validate(wateringDto);
        Optional<ConstraintViolation<WateringDto>> error = errors.stream().min(Comparator.comparing(i -> i.getPropertyPath().toString()));
        if (error.isPresent()) {
            throw new ValidationException(error.get().getPropertyPath() + " " + error.get().getMessage());
        }
        Valve valve = state.findValve(wateringDto.getValveId());
        wateringDto.setValve(valve);
        return wateringDto;
    }
}
