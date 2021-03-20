package com.github.lulewiczg.watering.service.ina219.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum for the PGA gain and range setting options.
 */
@Getter
@RequiredArgsConstructor
public enum Pga {
    GAIN_1(0), // 1
    GAIN_2(1), // /2
    GAIN_4(2), // /4
    GAIN_8(3); // /8

    private final int value;
}