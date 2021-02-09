package com.github.lulewiczg.watering.state;

import com.github.lulewiczg.watering.service.dto.ActionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for transferring commands from master.
 */
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConditionalOnProperty(name = "com.github.lulewiczg.watering.role", havingValue = "master")
public class MasterState {

    private List<ActionDto> actions = new ArrayList<>();

    private List<String> jobs = new ArrayList<>();
}
