package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.state.MasterState;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for jobs in master server to pass them back to slave server.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/jobs")
@ConditionalOnBean(MasterConfig.class)
public class JobMasterController {

    private final ActionService actionService;

    private final MasterState masterState;

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public List<JobDefinitionDto> getJobs() {
        return actionService.getJobs();
    }

    @GetMapping("/pending")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public List<JobDto> getPendingJobs() {
        return masterState.getJobs();
    }

    @GetMapping("/results")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public List<ActionResultDto<?>> getResults() {
        return masterState.getJobResults();
    }

    @PostMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public void runJob(@RequestBody JobDto job) {
        actionService.runJob(job);
    }

}
