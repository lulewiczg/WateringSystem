package com.github.lulewiczg.watering.service.actions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DTO for watering action.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WateringDto {

    @NotEmpty
    private String valveId;

    @JsonIgnore
    private Valve valve;

    @Min(1)
    @Max(7200)//2h
    @NotNull
    private Integer seconds;

    @JsonIgnore
    private AtomicInteger counter = new AtomicInteger(1);

}
