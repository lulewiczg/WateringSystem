package com.github.lulewiczg.watering.service.actions;

import lombok.SneakyThrows;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

/**
 * Interface for system action.
 *
 * @param <T> action parameter type
 * @param <R> action return type
 */
public interface Action<T, R> {

    /**
     * Executes action
     *
     * @param param acton param
     * @return action result
     */
    R doAction(T param);

    /**
     * Checks if action is enabled.
     *
     * @return true, if enabled
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * Gets parameter type.
     *
     * @return type
     */
    @SneakyThrows
    default String getParamType() {
        return Void.class.getSimpleName();
    }

    /**
     * Returns parameter description.
     *
     * @return param description
     */
    default String getParamDescription() {
        return "";
    }

    /**
     * Gets return type.
     *
     * @return type
     */
    @SneakyThrows
    default String getReturnType() {
        ParameterizedType type = findType();
        return Class.forName(type.getActualTypeArguments()[1].getTypeName()).getSimpleName();
    }

    private ParameterizedType findType() {
        return Arrays.stream(this.getClass().getGenericInterfaces())
                .map(i -> (ParameterizedType) i).filter(i -> i.getRawType() == Action.class).findFirst().orElseThrow();
    }
}
