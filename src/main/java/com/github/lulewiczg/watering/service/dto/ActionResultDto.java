package com.github.lulewiczg.watering.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for action result;
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionResultDto<T> {

    private String id;

    private T result;

    private LocalDateTime execDate;

    private String errorMsg;

    public ActionResultDto(String id, T result, LocalDateTime execDate) {
        this.id = id;
        this.result = result;
        this.execDate = execDate;
    }

    public ActionResultDto(String id, LocalDateTime execDate, String errorMsg) {
        this.id = id;
        this.execDate = execDate;
        this.errorMsg = errorMsg;
    }
}
