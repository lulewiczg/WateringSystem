package com.github.lulewiczg.watering.service.actions.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * DTO fro watering action.
 */
@Data
public class WateringDto {

    @Size(min = 1)
    private List<WateringEntryDto> data;

}
