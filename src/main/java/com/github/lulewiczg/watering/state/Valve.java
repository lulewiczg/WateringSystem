package com.github.lulewiczg.watering.state;

import com.github.lulewiczg.watering.config.dto.ValveConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Valve status.
 */
@Data
@RequiredArgsConstructor
public class Valve {

    private final ValveConfig config;

    private boolean opened;

}
