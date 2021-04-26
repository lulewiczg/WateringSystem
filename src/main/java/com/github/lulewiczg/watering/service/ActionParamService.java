package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.exception.ValidationException;
import com.github.lulewiczg.watering.service.actions.dto.WateringDto;
import com.github.lulewiczg.watering.service.actions.dto.WateringDtoMapper;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
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

    private final WateringDtoMapper wateringDtoMapper;

    private final LocalValidatorFactoryBean validator;

    private final Map<Class<?>, Function<ActionDto, Object>> HANDLERS = Map.of(
            Valve.class, i -> getState().findValve(i.getParam().toString()),
            Sensor.class, i -> getState().findSensor(i.getParam().toString()),
            WateringDto.class, this::handleWateringDto);

    @SneakyThrows
    public Object mapParam(ActionDto actionDto, ActionDefinitionDto definition) {
        Class<?> destType = definition.getParameterDestinationType();
        Function<ActionDto, Object> handler = HANDLERS.get(destType);
        if (handler != null) {
            return handler.apply(actionDto);
        }
        return null;
    }

    private WateringDto handleWateringDto(ActionDto actionDto) {
        WateringDto wateringDto = wateringDtoMapper.map((Map<String, Object>) actionDto.getParam());
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
