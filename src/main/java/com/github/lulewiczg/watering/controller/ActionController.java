package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for actions.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/actions")
public class ActionController {

    private final ActionService actionService;

    @GetMapping("/actions")
    public List<ActionDefinitionDto> getActions() {
        return actionService.getActions();
    }

    @PostMapping("/actions")
    public Object runAction(@Valid @RequestBody ActionDto actionDto) {
        return actionService.runAction(actionDto);
    }

    @GetMapping("/jobs")
    public List<JobDefinitionDto> getJobs() {
        return actionService.getJobs();
    }

    @PostMapping("/jobs/{job}")
    public void runJob(@PathVariable("job") String name) {
        actionService.runJob(name);
    }

}
