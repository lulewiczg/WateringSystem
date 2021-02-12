package com.github.lulewiczg.watering.exception;

/**
 * Exception for invalid action parameter type.
 */
public class TypeMismatchException extends RuntimeException {

    public TypeMismatchException(Object value, Class<?> type) {
        super(String.format("[%s] is not valid value for %s type!", value, type));
    }
}
