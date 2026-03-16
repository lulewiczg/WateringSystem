package com.github.lulewiczg.watering.config.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

/**
 * Pump configuration.
 */
@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class PumpConfig implements PinnableConfig {

    @NotEmpty
    private String id;

    private String name;

    private Integer pin;

}
