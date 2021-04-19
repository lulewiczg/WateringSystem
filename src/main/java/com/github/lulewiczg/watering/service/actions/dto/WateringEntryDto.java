package com.github.lulewiczg.watering.service.actions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * Entry watering entry DTO.
 */
@Data
public class WateringEntryDto {

    @NotEmpty
    private String valveId;

    @JsonIgnore
    private Valve valve;

    @Min(1)
    @Max(7200)//2h
    private long seconds;
}
