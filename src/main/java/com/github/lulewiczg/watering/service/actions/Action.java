package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * Interface for system action.
 *
 * @param <T> action parameter type
 * @param <R> action return type
 */
@Log4j2
public abstract class Action<T, R> {

    /**
     * Executes action.
     *
     * @param param acton param
     * @return action result
     */
    abstract R doAction(ActionDto actionDto, T param);

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
     * @return allowed values
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

    /**
     * Returns ID for nested call.
     *
     * @param action action
     * @return ID
     */
    protected String getNestedId(ActionDto action) {
        return action.getId() + ".";
    }

    /**
     * Handles action result and throws exception if failed.
     *
     * @param result result
     */
    protected void handleResult(ActionResultDto<?> result) {
        if (result.getErrorMsg() != null) {
            throw new ActionException(result.getId(), result.getErrorMsg());
        }
    }

    /**
     * Runs nested action and checks result.
     *
     * @param runner       action runner
     * @param actionDto    action DTO
     * @param nestedAction nested action
     * @param param        nested action param
     * @param <T2>         nested action param type
     * @param <R2>         nested action return type
     * @return nesed action result
     */
    protected <T2, R2> ActionResultDto<R2> runNested(ActionRunner runner, ActionDto actionDto, Action<T2, R2> nestedAction, T2 param) {
        ActionResultDto<R2> result = runner.run(getNestedId(actionDto), nestedAction, param);
        handleResult(result);
        return result;
    }
}
