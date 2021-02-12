package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.*;
import com.github.lulewiczg.watering.state.MasterState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ActionResultDto<Void> runJob(JobDto jobName) {
        validateJob(jobName);
        state.getJobs().add(jobName);
        return null;
    }

    @Override
    public Object runAction(ActionDto actionDto) {
        validateAndGetDefinition(actionDto);
        state.getActions().add(actionDto);
        return null;
    }

}
