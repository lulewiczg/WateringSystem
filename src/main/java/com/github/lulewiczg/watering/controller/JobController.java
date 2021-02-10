package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for jobs.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/jobs")
@ConditionalOnMissingBean(ActionMasterController.class)
public class JobController {

    private final ActionService actionService;

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public List<JobDefinitionDto> getJobs() {
        return actionService.getJobs();
    }

    @PostMapping("/{job}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public void runJob(@PathVariable("job") String name) {
        actionService.runJob(name);
    }

}
