package com.github.lulewiczg.watering.exception;

import lombok.Data;

import java.util.Date;

/**
 * DTO for API errors.
 */
@Data
public class ApiError {

    private Date timestamp = new Date();

    private int status;

    private String error;

    private String message;

    public ApiError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
