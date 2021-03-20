package com.github.lulewiczg.watering.service.ina219.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum for the Bus Voltage Range setting (BRNG)
 */
@Getter
@RequiredArgsConstructor
public enum VoltageRange {
    V16(0), // 16 Volts
    V32(1); // 32 Volts

    private final int value;

}

  