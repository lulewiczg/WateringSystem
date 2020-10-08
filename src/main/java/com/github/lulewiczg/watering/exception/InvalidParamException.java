package com.github.lulewiczg.watering.exception;

import lombok.Getter;

@Getter
public class InvalidParamException extends RuntimeException {

    private final Class<?> type;

    private final Class<?> expected;

    public InvalidParamException(Class<?> expected, Class<?> type) {
        super(String.format("Invalid action parameter, expected %s, got %s", expected, type));
        this.type = type;
        this.expected = expected;
    }
}
