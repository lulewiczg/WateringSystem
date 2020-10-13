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
    ADMIN("ADMIN");

    private final String name;
}
