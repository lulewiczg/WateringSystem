package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.exception.ActionNotFoundException;
import com.github.lulewiczg.watering.service.actions.Action;
import com.github.lulewiczg.watering.service.dto.*;
import com.github.lulewiczg.watering.service.job.ScheduledJob;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    private final AppState state;

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

    @Override
    @Cacheable
    public List<JobDefinitionDto> getJobs() {
        return applicationContext.getBeansOfType(ScheduledJob.class).values().stream()
                .map(i -> new JobDefinitionDto(fixBeanName(i.getClass().getSimpleName()), i.canBeStarted()))
                .collect(Collectors.toList());
    }

    @Override
    public ActionResultDto<Void> runJob(JobDto jobDto) {
        validateJob(jobDto);
        ScheduledJob job;
        try {
            job = applicationContext.getBean(jobDto.getName(), ScheduledJob.class);
        } catch (NoSuchBeanDefinitionException e) {
            log.error(e);
            throw new IllegalArgumentException("Job not found: " + jobDto.getName());
        }
        log.trace("Running job {}", job::getName);
        return job.run(jobDto);
    }

    @Override
    @SneakyThrows
    public Object runAction(ActionDto actionDto) {
        ActionDefinitionDto actionDef = validateAndGetDefinition(actionDto);
        Action<?, ?> action = Optional.of(applicationContext.getBean(actionDto.getName(), Action.class))
                .filter(Action::isEnabled).orElseThrow(() -> new IllegalArgumentException("Action not found: " + actionDto.getName()));
        Object param = getParam(actionDto, actionDef);
        Method method = getMethod(actionDef, action);
        log.trace("Running action {} with param {}", actionDto.getName(), actionDto.getParam());
        return method.invoke(action, param);
    }

    private Method getMethod(ActionDefinitionDto actionDef, Action<?, ?> action) {
        List<Method> methods = Arrays.stream(action.getClass().getMethods())
                .filter(i -> i.getName().equals("doAction")).collect(Collectors.toList());
        Method method;
        if (methods.size() > 1) {
            method = methods.stream().filter(i -> i.getParameters()[0].getType() != Object.class).findFirst().orElseThrow();
        } else {
            method = methods.get(0);
        }
        log.trace(method);
        Class<?> methodParamType = method.getParameters()[0].getType();
        if (methodParamType != actionDef.getParameterDestinationType()) {
            log.error("Method parameter types mismatch ({} vs {})", methodParamType, actionDef.getParameterDestinationType());
            throw new ActionNotFoundException(actionDef.getActionName());
        }
        return method;
    }

    private Object getParam(ActionDto actionDto, ActionDefinitionDto definition) {
        if (Valve.class.equals(definition.getParameterDestinationType())) {
            return state.findValve(actionDto.getParam().toString());
        } else if (Sensor.class.equals(definition.getParameterDestinationType())) {
            return state.findSensor(actionDto.getParam().toString());
        }
        //Only Valve and Sensor supported for now
        return null;
    }

    private String fixBeanName(String i) {
        return i.substring(0, 1).toLowerCase() + i.substring(1);
    }
}
