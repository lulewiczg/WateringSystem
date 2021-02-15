package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Interface for system action.
 *
 * @param <T> action parameter type
 * @param <R> action return type
 */
@Log4j2
public abstract class Action<T, R> {

    /**
     * Runs action.
     *
     * @param originalDto action DTO
     * @param param     parameter
     * @return action result
     */
    public ActionResultDto<R> doAction(@NonNull ActionDto originalDto, T param) {
        ActionDto actionDto = originalDto.toBuilder().build();
        generateUuid(actionDto);
        String id = actionDto.getId();
        log.debug("Executing {} action with ID {} ...", actionDto.getName(), id);
        R result;
        try {
            result = doActionInternal(actionDto, param);
        } catch (Exception e) {
            log.error(String.format("Action %s failed", id), e);
            String message = e.getMessage();
            if (message == null) {
                message = "Unknown error!";
            }
            return new ActionResultDto<>(id, LocalDateTime.now(), message);
        }
        return new ActionResultDto<>(id, result, LocalDateTime.now());
    }

    /**
     * Generates UUID if required
     *
     * @param actionDto action
     */
    private void generateUuid(ActionDto actionDto) {
        String id = actionDto.getId();
        if (id == null) {
            log.debug("No UUID passed, generating new");
            actionDto.setId(UUID.randomUUID().toString());
        } else if (id.endsWith(".")) {
            log.debug("Nested invocation, appending new id...");
            actionDto.appendId(UUID.randomUUID().toString());
        }
    }

    /**
     * Executes action
     *
     * @param param acton param
     * @return action result
     */
    protected abstract R doActionInternal(ActionDto actionDto, T param);

    /**
     * Gets action description.
     *
     * @return description
     */
    public abstract String getDescription();

    /**
     * Checks if action is enabled.
     *
     * @return true, if enabled
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * Gets parameter type.
     *
     * @return type
     */
    public Class<?> getParamType() {
        return Void.class;
    }


    /**
     * Gets destination parameter type.
     *
     * @return type
     */
    public Class<?> getDestinationParamType() {
        return Void.class;
    }

    /**
     * Gets allowed values for action
     *
     * @return allowewd values
     */
    public List<?> getAllowedValues() {
        return null;
    }

    /**
     * Returns parameter description.
     *
     * @return param description
     */
    public String getParamDescription() {
        return "";
    }

    /**
     * Gets return type.
     *
     * @return type
     */
    public Class<?> getReturnType() {
        return Void.class;
    }

}
