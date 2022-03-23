package com.github.lulewiczg.watering.exception;

/**
 * Exception for missing parameter handler.
 */
public class HandlerNotFoundException extends RuntimeException {

    public HandlerNotFoundException(Object value, Class<?> type) {
        super(String.format("No handler found to handle [%s] as [%s]!", value, type));
    }
}
