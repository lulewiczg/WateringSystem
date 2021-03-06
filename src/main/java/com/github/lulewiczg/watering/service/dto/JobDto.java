package com.github.lulewiczg.watering.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lulewiczg.watering.service.job.ScheduledJob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * DTO for job.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class JobDto {

    private String id;

    @NotNull
    private String name;

    @JsonIgnore
    private ScheduledJob job;

    /**
     * Appends string to ID
     *
     * @param id id
     */
    public void appendId(String id) {
        if (id == null) {
            throw new IllegalStateException("ID is null!");
        }
        this.id += id;
    }

    public JobDto(String name) {
        this.name = name;
    }

    public JobDto(String id, String name) {
        this.name = name;
        this.id = id;
    }
}
