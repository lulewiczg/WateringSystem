package com.github.lulewiczg.watering.service.actions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;

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
    private Instant startDate;

    @JsonProperty
    public long getSecondsLeft() {
        return seconds - (Instant.now().getEpochSecond() - startDate.getEpochSecond());
    }

}
