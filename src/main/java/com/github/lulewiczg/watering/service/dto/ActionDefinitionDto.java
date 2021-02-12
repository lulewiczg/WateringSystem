package com.github.lulewiczg.watering.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for action definition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionDefinitionDto {

    private String actionName;

    private String description;

    private Class<?> parameterType;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Class<?> parameterDestinationType;

    private List<?> allowedValues;

    private String parameterDescription;

    private Class<?> returnType;

}
