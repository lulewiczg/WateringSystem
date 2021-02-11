package com.github.lulewiczg.watering.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

/**
 * DTO for action.
 */
@Data
@Valid
@NoArgsConstructor
@AllArgsConstructor
public class ActionDto {

    private String name;

    private String paramType;

    private String param;
}
