package com.github.lulewiczg.watering.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for action definition.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionDefinitionDto {

    private String actionName;

    private String parameterType;

    private String parameterDescription;

    private String returnType;
}
