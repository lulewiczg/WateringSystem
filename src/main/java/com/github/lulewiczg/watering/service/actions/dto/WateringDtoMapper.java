package com.github.lulewiczg.watering.service.actions.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

/**
 * Mapper for Watering action DTO.
 */
@Mapper(componentModel = "spring")
public interface WateringDtoMapper {

    @Mapping(expression = "java((String) dto.get(\"valveId\"))", target = "valveId")
    @Mapping(expression = "java((Integer) dto.get(\"seconds\"))", target = "seconds")
    WateringDto map(Map<String, Object> dto);
}
