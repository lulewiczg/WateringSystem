package com.github.lulewiczg.watering.service.actions;

import lombok.SneakyThrows;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

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
     * Gets action description.
     *
     * @return description
     */
    String getDescription();

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
    default Class<?> getParamType() {
        return Void.class;
    }


    /**
     * Gets destination parameter type.
     *
     * @return type
     */
    default Class<?> getDestinationParamType() {
        return Void.class;
    }

    /**
     * Gets allowed values for action
     *
     * @return allowewd values
     */
    default List<?> getAllowedValues() {
        return null;
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
    default Class<?> getReturnType() {
        ParameterizedType type = findType();
        return Class.forName(type.getActualTypeArguments()[1].getTypeName());
    }

    private ParameterizedType findType() {
        return Arrays.stream(this.getClass().getGenericInterfaces())
                .map(ParameterizedType.class::cast).filter(i -> i.getRawType() == Action.class).findFirst().orElseThrow();
    }
}
