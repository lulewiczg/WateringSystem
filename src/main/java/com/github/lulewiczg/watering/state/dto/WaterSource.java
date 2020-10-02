package com.github.lulewiczg.watering.state.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base class for water source.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterSource {

    private Valve valve;
}
