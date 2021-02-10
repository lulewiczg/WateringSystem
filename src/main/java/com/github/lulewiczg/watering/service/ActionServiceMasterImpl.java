package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import com.github.lulewiczg.watering.state.MasterState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling actions.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(ActionServiceImpl.class)
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
    public void runJob(String jobName) {
        state.getJobDefinitions().stream().filter(i -> i.getJobName().equals(jobName)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobName));
        state.getJobs().add(jobName);
    }

    @Override
    public Object runAction(ActionDto actionDto) {
        state.getActionDefinitions().stream().filter(i -> i.getActionName().equals(actionDto.getName()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Job not found: " + actionDto));
        state.getActions().add(actionDto);
        return null;
    }

}
