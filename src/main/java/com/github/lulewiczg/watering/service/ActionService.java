package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.exception.ActionNotFoundException;
import com.github.lulewiczg.watering.exception.JobNotFoundException;
import com.github.lulewiczg.watering.exception.TypeMismatchException;
import com.github.lulewiczg.watering.exception.ValueNotAllowedException;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ParamType;

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

    /**
     * Finds action definition and validates parameter.
     *
     * @param action action
     * @return action definition
     */
    default ActionDefinitionDto validateAndGetDefinition(ActionDto action) {
        ActionDefinitionDto actionDef = getActions().stream().filter(i -> i.getActionName().equals(action.getName())).findFirst()
                .orElseThrow(() -> new ActionNotFoundException(action.getName()));

        boolean valid = ParamType.getByClass(actionDef.getParameterType()).getValidation().test(action.getParam());
        if (!valid) {
            throw new TypeMismatchException(action.getParam(), actionDef.getParameterType());
        }
        if (actionDef.getAllowedValues() != null && !actionDef.getAllowedValues().contains(action.getParam())) {
            throw new ValueNotAllowedException(action.getParam(), actionDef.getAllowedValues());
        }
        return actionDef;
    }

    default void validateJob(String jobName) {
        getJobs().stream().filter(i -> i.getJobName().equals(jobName) && i.isCanBeRun()).findFirst()
                .orElseThrow(() -> new JobNotFoundException(jobName));
    }
}
