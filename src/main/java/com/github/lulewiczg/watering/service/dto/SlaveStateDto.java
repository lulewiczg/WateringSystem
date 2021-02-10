package com.github.lulewiczg.watering.service.dto;

import com.github.lulewiczg.watering.state.AppState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlaveStateDto {

    private AppState state;

    private List<ActionDefinitionDto> actions;

    private List<JobDefinitionDto> jobs;

}
