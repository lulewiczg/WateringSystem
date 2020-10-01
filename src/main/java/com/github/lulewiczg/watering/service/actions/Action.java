package com.github.lulewiczg.watering.service.actions;

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

}
