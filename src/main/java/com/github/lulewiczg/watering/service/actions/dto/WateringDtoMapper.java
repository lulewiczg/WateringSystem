package com.github.lulewiczg.watering.service.actions.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Map;

/**
 * Mapper for Watering action DTO.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WateringDtoMapper {

    @Mapping(expression = "java((String) dto.get(\"valveId\"))", target = "valveId")
    @Mapping(expression = "java((Integer) dto.get(\"seconds\"))", target = "seconds")
    @Mapping(target = "valve", ignore = true)
    @Mapping(target = "counter", ignore = true)
    WateringDto map(Map<String, Object> dto);
}
