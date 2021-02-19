package com.github.lulewiczg.watering.exception;

/**
 * Exception for action that can not be started.
 */
public class ActionNotStartedException extends RuntimeException {

    public ActionNotStartedException(String name) {
        super(String.format("Action [%s] can not be started!", name));
    }
}
