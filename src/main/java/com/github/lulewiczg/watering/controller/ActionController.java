package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.ActionDto;
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
    public List<String> getActions() {
        return actionService.getActions();
    }

    @PostMapping("/actions")
    public Object runAction(@Valid @RequestBody ActionDto actionDto) {
        return actionService.runAction(actionDto);
    }

    @GetMapping("/jobs")
    public List<String> getJobs() {
        return actionService.getJobs();
    }

    @PostMapping("/jobs/{job}")
    public void runJob(@PathVariable("job") String name) {
        actionService.runJob(name);
    }

}
