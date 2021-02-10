package com.github.lulewiczg.watering.state.dto;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import java.util.List;

/**
 * DTO for transferring commands from master.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConditionalOnBean(MasterConfig.class)
public class MasterResponse {

    private List<ActionDto> actions;

    private List<String> jobs;

}
