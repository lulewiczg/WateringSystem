package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for jobs.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/jobs")
public class JobController {

    private final ActionService actionService;

    @GetMapping
    public List<JobDefinitionDto> getJobs() {
        return actionService.getJobs();
    }

    @PostMapping("/{job}")
    public void runJob(@PathVariable("job") String name) {
        actionService.runJob(name);
    }

}
