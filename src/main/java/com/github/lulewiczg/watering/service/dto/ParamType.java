package com.github.lulewiczg.watering.service.dto;

import com.github.lulewiczg.watering.service.actions.dto.WateringDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Enum for parameter type.
 */
@Getter
@RequiredArgsConstructor
public enum ParamType {

    STRING(String.class, i -> i != null && i.getClass() == String.class),
    VOID(Void.class, Objects::isNull), WATERING_DTO(WateringDto.class, i -> true);

    private final Class<?> type;

    private final Predicate<Object> validation;

    /**
     * Finds param type by class.
     *
     * @param clazz class
     * @return type
     */
    public static ParamType getByClass(Class<?> clazz) {
        return Arrays.stream(values()).filter(i -> i.getType().equals(clazz)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Type not found: " + clazz));
    }

}
