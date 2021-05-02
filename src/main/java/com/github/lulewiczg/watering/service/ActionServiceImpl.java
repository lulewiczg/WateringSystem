package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.actions.Action;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.dto.*;
import com.github.lulewiczg.watering.service.job.JobRunner;
import com.github.lulewiczg.watering.service.job.ScheduledJob;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for handling actions.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class ActionServiceImpl implements ActionService {

    private final ApplicationContext applicationContext;

    private final ActionRunner actionRunner;

    private final JobRunner jobRunner;

    private final ActionParamService actionParamService;

    @Override
    @Cacheable
    public List<ActionDefinitionDto> getActions() {
        return applicationContext.getBeansOfType(Action.class).values().stream().filter(Action::isEnabled)
                .map(i -> ActionDefinitionDto.builder()
                        .actionName(fixBeanName(i.getClass().getSimpleName()))
                        .description(i.getDescription())
                        .parameterType(i.getParamType())
                        .parameterDestinationType(i.getDestinationParamType())
                        .allowedValues(i.getAllowedValues())
                        .parameterDescription(i.getParamDescription())
                        .returnType(i.getReturnType())
                        .build())
                .collect(Collectors.toList());
    }

    @Cacheable
    private Map<String, Action<?, ?>> getActionsMap() {
        return applicationContext.getBeansOfType(Action.class).values().stream().filter(Action::isEnabled).map(i -> (Action<?, ?>) i)
                .collect(Collectors.toMap(i -> fixBeanName(i.getClass().getSimpleName()), i -> i));
    }

    @Override
    @Cacheable
    public List<JobDefinitionDto> getJobs() {
        return applicationContext.getBeansOfType(ScheduledJob.class).values().stream()
                .map(i -> new JobDefinitionDto(fixBeanName(i.getClass().getSimpleName()), i.canBeStarted()))
                .collect(Collectors.toList());
    }

    @Override
    public ActionResultDto<?> runJob(JobDto jobDto) {
        validateJob(jobDto);
        ScheduledJob job;
        try {
            job = applicationContext.getBean(jobDto.getName(), ScheduledJob.class);
        } catch (NoSuchBeanDefinitionException e) {
            log.error(e);
            throw new IllegalArgumentException("Job not found: " + jobDto.getName());
        }
        log.trace("Running job {}", job.getName());
        jobDto.setJob(job);
        return jobRunner.run(jobDto);
    }

    @Override
    @SneakyThrows
    public ActionResultDto<?> runAction(ActionDto actionDto) {
        ActionDefinitionDto actionDef = validateAndGetDefinition(actionDto);
        Action<?, ?> action = getActionsMap().get(actionDef.getActionName());
        if (action == null) {
            throw new IllegalArgumentException("Action not found: " + actionDto.getName());
        }
        try {
            Object param = actionParamService.mapParam(actionDef, actionDto.getParam());
            actionDto.setAction(action);
            return actionRunner.run(actionDto, param);
        } catch (Exception e) {
            log.error("Error while running action {}", actionDto.getName());
            throw e;
        }
    }


    private String fixBeanName(String i) {
        return i.substring(0, 1).toLowerCase() + i.substring(1);
    }
}
