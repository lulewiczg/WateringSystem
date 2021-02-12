package com.github.lulewiczg.watering.exception;

/**
 * Exception for invalid action parameter.
 */
public class InvalidParamException extends RuntimeException {

    public InvalidParamException(String expected, String type) {
        super(String.format("Invalid action parameter, expected %s, got %s", expected, type));

    }
}
