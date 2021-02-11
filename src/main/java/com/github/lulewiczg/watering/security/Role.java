package com.github.lulewiczg.watering.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Roles for security in application.
 */
@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("GUEST"),
    USER("USER"),
    SLAVE("SLAVE"),
    ADMIN("ADMIN");

    private final String name;
}
