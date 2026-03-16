package com.github.lulewiczg.watering.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lulewiczg.watering.service.actions.Action;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for action.
 */
@Data
@Valid
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ActionDto {

    @NotNull
    @NotEmpty
    private String name;

    private String id;

    @JsonIgnore
    private Action<?, ?> action;

    private Object param;

    /**
     * Appends string to ID
     *
     * @param id id
     */
    public void appendId(String id) {
        if (id == null) {
            throw new IllegalStateException("ID is null!");
        }
        this.id += id;
    }

    public ActionDto(@NotNull @NotEmpty String name, Object param) {
        this.name = name;
        this.param = param;
    }

    public ActionDto(@NotNull @NotEmpty String name, String id, Action<?, ?> action) {
        this.name = name;
        this.id = id;
        this.action = action;
    }

    public ActionDto(String id) {
        this.id = id;
    }
}
