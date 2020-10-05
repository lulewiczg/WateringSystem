package com.github.lulewiczg.watering.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum for current system status.
 */
@Getter
@RequiredArgsConstructor
public enum SystemStatus {

    IDLE, WATERING, DRAINING, FILLING, ERROR

}
