package com.github.lulewiczg.watering.config.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

/**
 * Water valve config.
 */
@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class ValveConfig implements PinnableConfig {

    @NotEmpty
    private String id;

    @NotEmpty
    private String name;

    @NotNull
    private ValveType type;

    private Integer pin;

    private boolean open;

    private boolean overflowOutput = false;

    @Min(1)
    @Max(7200)
    private Long wateringTime;

}
