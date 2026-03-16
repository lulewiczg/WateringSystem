package com.github.lulewiczg.watering.service.actions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lulewiczg.watering.state.dto.Valve;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    @EqualsAndHashCode.Exclude
    private AtomicInteger counter = new AtomicInteger(1);

}
