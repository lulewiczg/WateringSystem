package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.exception.InvalidParamException;
import com.github.lulewiczg.watering.service.actions.Action;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import com.github.lulewiczg.watering.service.job.ScheduledJob;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for handling acitons.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class ActionService {

    private final ApplicationContext applicationContext;

    private final AppState state;

    /**
     * Returns active actions.
     *
     * @return actions
     */
    public List<ActionDefinitionDto> getActions() {
        return applicationContext.getBeansOfType(Action.class).values().stream().filter(Action::isEnabled)
                .map(i -> new ActionDefinitionDto(fixBeanName(i.getClass().getSimpleName()),
                        i.getParamType(), i.getParamDescription(), i.getReturnType()))
                .collect(Collectors.toList());
    }

    /**
     * Returns active jobs.
     *
     * @return jobs
     */
    public List<JobDefinitionDto> getJobs() {
        return applicationContext.getBeansOfType(ScheduledJob.class).values().stream()
                .map(i -> new JobDefinitionDto(fixBeanName(i.getClass().getSimpleName()), i.canBeStarted()))
                .collect(Collectors.toList());
    }

    /**
     * Runs job
     *
     * @param jobName job name
     */
    public void runJob(String jobName) {
        ScheduledJob job;
        try {
            job = applicationContext.getBean(jobName, ScheduledJob.class);
        } catch (NoSuchBeanDefinitionException e) {
            log.error(e);
            throw new IllegalArgumentException("Job not found: " + jobName);
        }
        log.trace("Running job {}", job::getName);
        job.run();
    }

    /**
     * Runs action.
     *
     * @param actionDto action details
     */
    @SneakyThrows
    public Object runAction(ActionDto actionDto) {
        Action<?, ?> action = Optional.of(applicationContext.getBean(actionDto.getName(), Action.class))
                .filter(Action::isEnabled).orElseThrow(() -> new IllegalArgumentException("Action not found: " + actionDto.getName()));
        Object param = getParam(actionDto);
        Method method = getMethod(action, param);
        log.trace("Running action {} with param {}", actionDto.getName(), actionDto.getParam());
        return method.invoke(action, param);
    }

    private Method getMethod(Action<?, ?> action, Object param) {
        Class<?> paramType = Optional.ofNullable(param).map(Object::getClass).orElse(null);
        List<Method> methods = Arrays.stream(action.getClass().getMethods())
                .filter(i -> i.getName().equals("doAction")).collect(Collectors.toList());
        Method method;
        if (methods.size() > 1) {
            method = methods.stream().filter(i -> i.getParameters()[0].getType() != Object.class).findFirst().orElseThrow();
        } else {
            method = methods.get(0);
        }
        log.info(method);
        Class<?> expectedType = method.getParameters()[0].getType();
        if (param == null && expectedType != Void.class || paramType != expectedType) {
            throw new InvalidParamException(expectedType, paramType);
        }
        return method;
    }

    private Object getParam(ActionDto actionDto) {
        if (Valve.class.getSimpleName().equals(actionDto.getParamType())) {
            return state.findValve(actionDto.getParam());
        } else if (Sensor.class.getSimpleName().equals(actionDto.getParamType())) {
            return state.findSensor(actionDto.getParam());
        }
        return null;
    }


    private String fixBeanName(String i) {
        return i.substring(0, 1).toLowerCase() + i.substring(1);
    }
}
