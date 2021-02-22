package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.*;
import com.github.lulewiczg.watering.state.MasterState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for handling actions.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnBean(MasterConfig.class)
public class ActionServiceMasterImpl implements ActionService {

    private final MasterState state;

    @Override
    public List<ActionDefinitionDto> getActions() {
        return state.getActionDefinitions();
    }

    @Override
    public List<JobDefinitionDto> getJobs() {
        return state.getJobDefinitions();
    }

    @Override
    public ActionResultDto<?> runJob(JobDto job) {
        validateJob(job);
        job.setId(UUID.randomUUID().toString());
        state.getJobs().add(job);

        return ActionResultDto.builder()
                .id(job.getId())
                .actionName(job.getName())
                .build();
    }

    @Override
    public ActionResultDto<?> runAction(ActionDto actionDto) {
        validateAndGetDefinition(actionDto);
        actionDto.setId(UUID.randomUUID().toString());
        state.getActions().add(actionDto);
        return ActionResultDto.builder()
                .id(actionDto.getId())
                .actionName(actionDto.getName())
                .build();
    }

}
