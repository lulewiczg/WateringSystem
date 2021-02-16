package com.github.lulewiczg.watering.state;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for transferring commands from master.
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConditionalOnBean(MasterConfig.class)
public class MasterState {

    private List<ActionDto> actions = new ArrayList<>();

    private List<JobDto> jobs = new ArrayList<>();

    private List<ActionDefinitionDto> actionDefinitions = new ArrayList<>();

    private List<JobDefinitionDto> jobDefinitions = new ArrayList<>();

    private List<ActionResultDto<?>> jobResults = new ArrayList<>();

    private List<ActionResultDto<?>> actionResults = new ArrayList<>();

}
