package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;

import java.util.List;

/**
 * Interface for handling actions.
 */
public interface ActionService {

    /**
     * Returns active actions.
     *
     * @return actions
     */
    List<ActionDefinitionDto> getActions();

    /**
     * Returns active jobs.
     *
     * @return jobs
     */
    List<JobDefinitionDto> getJobs();

    /**
     * Runs job
     *
     * @param jobName job name
     */
    void runJob(String jobName);

    /**
     * Runs action.
     *
     * @param actionDto action details
     */
    Object runAction(ActionDto actionDto);
}
