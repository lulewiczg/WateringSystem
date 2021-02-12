package com.github.lulewiczg.watering.service.actions;

import java.util.List;

/**
 * Interface for system action.
 *
 * @param <T> action parameter type
 * @param <R> action return type
 */
public abstract class Action<T, R> {

    /**
     * Executes action
     *
     * @param param acton param
     * @return action result
     */
    public abstract R doAction(T param);

    /**
     * Gets action description.
     *
     * @return description
     */
    public abstract String getDescription();

    /**
     * Checks if action is enabled.
     *
     * @return true, if enabled
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * Gets parameter type.
     *
     * @return type
     */
    public Class<?> getParamType() {
        return Void.class;
    }


    /**
     * Gets destination parameter type.
     *
     * @return type
     */
    public Class<?> getDestinationParamType() {
        return Void.class;
    }

    /**
     * Gets allowed values for action
     *
     * @return allowewd values
     */
    public List<?> getAllowedValues() {
        return null;
    }

    /**
     * Returns parameter description.
     *
     * @return param description
     */
    public String getParamDescription() {
        return "";
    }

    /**
     * Gets return type.
     *
     * @return type
     */
    public Class<?> getReturnType() {
        return Void.class;
    }

}
