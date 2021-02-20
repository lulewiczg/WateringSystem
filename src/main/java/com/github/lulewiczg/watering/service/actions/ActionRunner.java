package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Runner for actions.
 */
@Log4j2
@Component
public class ActionRunner {

    /**
     * Runs action.
     *
     * @param id     ID
     * @param action action
     * @param param  action param
     * @param <T>    action param type
     * @param <R>    action return type
     * @return action result
     */
    public <T, R> ActionResultDto<R> run(String id, Action<T, R> action, T param) {
        return run(new ActionDto(action.getClass().getSimpleName(), id, action, null), param);
    }

    /**
     * Runs action.
     *
     * @param originalDto action DTO
     * @param param       parameter
     * @return action result
     */
    public <T, R> ActionResultDto<R> run(@NonNull ActionDto originalDto, T param) {
        Action<T, R> action = (Action<T, R>) originalDto.getAction();
        ActionDto actionDto = originalDto.toBuilder().build();
        generateUuid(actionDto);
        String id = actionDto.getId();
        log.debug("Executing {} action with ID {} ...", actionDto.getName(), id);
        R result;
        try {
            result = action.run(actionDto, param);
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
}
