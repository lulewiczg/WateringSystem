package com.github.lulewiczg.watering.exception;

/**
 * Exception for actions errors.
 */
public class ActionException extends RuntimeException {

    public ActionException(String id, String error) {
        super(String.format("Action [%s] failed: %s", id, error));
    }
}
