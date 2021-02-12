package com.github.lulewiczg.watering.exception;

import java.util.List;

/**
 * Exception for invalid action parameter value.
 */
public class ValueNotAllowedException extends RuntimeException {

    public ValueNotAllowedException(Object value, List<?> allowed) {
        super(String.format("Value [%s] does not match %s!", value, allowed));
    }
}
