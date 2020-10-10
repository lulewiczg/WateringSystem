package com.github.lulewiczg.watering.exception;

import lombok.Getter;

@Getter
public class ValveNotFoundException extends RuntimeException {

    private final String id;

    public ValveNotFoundException(String id) {
        super("Valve not found: " + id);
        this.id = id;
    }
}
