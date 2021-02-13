package com.github.lulewiczg.watering.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

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

    private UUID id;

    private Object param;

    public ActionDto(@NotNull @NotEmpty String name, Object param) {
        this.name = name;
        this.param = param;
    }
}
