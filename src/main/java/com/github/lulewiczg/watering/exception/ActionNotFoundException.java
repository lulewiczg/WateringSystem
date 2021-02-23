package com.github.lulewiczg.watering.exception;

/**
 * Exception for not found action.
 */
public class ActionNotFoundException extends RuntimeException {

    public ActionNotFoundException(String id) {
        super("Action not found: " + id);
    }
}
