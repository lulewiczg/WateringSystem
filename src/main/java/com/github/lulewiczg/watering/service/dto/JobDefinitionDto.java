package com.github.lulewiczg.watering.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for job definition.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDefinitionDto {

    private String jobName;

    private boolean canBeRun;
}
