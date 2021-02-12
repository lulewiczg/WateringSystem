package com.github.lulewiczg.watering.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for action result;
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionResultDto<T> {

    private UUID id;

    private T result;

    private LocalDateTime execDate;

    private String errorMsg;

    public ActionResultDto(UUID id, T result, LocalDateTime execDate) {
        this.id = id;
        this.result = result;
        this.execDate = execDate;
    }

    public ActionResultDto(UUID id, LocalDateTime execDate, String errorMsg) {
        this.id = id;
        this.execDate = execDate;
        this.errorMsg = errorMsg;
    }
}
