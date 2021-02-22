package com.github.lulewiczg.watering.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for action result;
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionResultDto<T> {

    private String id;

    private String actionName;

    private T result;

    private LocalDateTime execDate;

    private String errorMsg;

}
