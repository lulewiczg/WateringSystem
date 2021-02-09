package com.github.lulewiczg.watering.state;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.EnumSet;

/**
 * Enum for current system status.
 */
@RequiredArgsConstructor
public enum SystemStatus {

    IDLE,
    WATERING,
    DRAINING,
    FILLING,
    ERROR;

    static {
        IDLE.setIncorruptibleBy(EnumSet.of(WATERING, DRAINING, FILLING, ERROR, IDLE));
        WATERING.setIncorruptibleBy(EnumSet.of(IDLE));
        DRAINING.setIncorruptibleBy(EnumSet.of(WATERING, IDLE));
        FILLING.setIncorruptibleBy(EnumSet.of(ERROR, DRAINING));
        ERROR.setIncorruptibleBy(EnumSet.noneOf(SystemStatus.class));
    }

    @Setter(AccessLevel.PRIVATE)
    private EnumSet<SystemStatus> incorruptibleBy;

    /**
     * Checks if system status can be interrupted by another
     *
     * @param status new status
     * @return true, if can be interrupted
     */
    public boolean canBeInterrupted(SystemStatus status) {
        return this != status && incorruptibleBy.contains(status);
    }

}
