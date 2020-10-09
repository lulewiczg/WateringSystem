package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.exception.InvalidParamException;
import com.github.lulewiczg.watering.service.actions.Action;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.job.ScheduledJob;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
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
public class ActionService {

    private final ApplicationContext applicationContext;

    private final AppState state;

    /**
     * Returns active actions.
     *
     * @return actions
     */
    public List<String> getActions() {
        return applicationContext.getBeansOfType(Action.class).values().stream().filter(Action::isEnabled)
                .map(i -> i.getClass().getSimpleName()).map(this::fixBeanNames).collect(Collectors.toList());
    }

    /**
     * Returns active jobs.
     *
     * @return jobs
     */
    public List<String> getJobs() {
        return applicationContext.getBeansOfType(ScheduledJob.class).values().stream()
                .map(i -> i.getClass().getSimpleName()).map(this::fixBeanNames).collect(Collectors.toList());
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
        Class<?> paramType = Optional.ofNullable(param).map(Object::getClass).orElse(null);
        Method method = Arrays.stream(action.getClass().getMethods()).filter(i -> i.getName().equals("doAction")).findFirst().orElseThrow();
        Class<?> expectedType = method.getParameters()[0].getType();
        if (param == null && expectedType != Void.class || paramType != expectedType) {
            throw new InvalidParamException(expectedType, paramType);
        }
        log.trace("Running action {} with param {}", actionDto.getName(), actionDto.getParam());
        return method.invoke(action, param);
    }

    private Object getParam(ActionDto actionDto) {
        if (Valve.class.getSimpleName().equals(actionDto.getParamType())) {
            return state.findValve(actionDto.getParam());
        } else if (Sensor.class.getSimpleName().equals(actionDto.getParamType())) {
            return state.findSensor(actionDto.getParam());
        }
        return null;
    }


    private String fixBeanNames(String i) {
        return i.substring(0, 1).toLowerCase() + i.substring(1);
    }
}