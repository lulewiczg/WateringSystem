package com.github.lulewiczg.watering.exception;

import lombok.Getter;

@Getter
public class SensorNotFoundException extends RuntimeException {

    private final String id;

    public SensorNotFoundException(String id) {
        super("Sensor not found: " + id);
        this.id = id;
    }
}
