package com.github.lulewiczg.watering.exception;

import lombok.Getter;

@Getter
public class PumpNotFoundException extends RuntimeException {

    private final String id;

    public PumpNotFoundException(String id) {
        super("Pump not found: " + id);
        this.id = id;
    }
}
