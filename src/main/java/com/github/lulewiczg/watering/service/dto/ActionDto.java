package com.github.lulewiczg.watering.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO for action.
 */
@Data
@Valid
@NoArgsConstructor
@AllArgsConstructor
public class ActionDto {

    @NotNull
    @NotEmpty
    private String name;

    private Object param;
}
