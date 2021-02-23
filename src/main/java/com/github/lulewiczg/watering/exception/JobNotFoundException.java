package com.github.lulewiczg.watering.exception;

/**
 * Exception for not found jon.
 */
public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(String id) {
        super("Job not found: " + id);
    }
}
