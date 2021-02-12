package com.github.lulewiczg.watering.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO for job.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {

    @NotNull
    private String name;

    private UUID id;

    public JobDto(@NotNull String name) {
        this.name = name;
    }
}
