package com.github.lulewiczg.watering.config.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Water valve config.
 */
@Data
@Validated
@AllArgsConstructor
@RequiredArgsConstructor
public class ValveConfig {

    @Valid
    @NotNull
    private String name;

    @Valid
    @NotNull
    private ValveType type;

    private boolean open;

}
