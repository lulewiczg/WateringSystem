package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.MasterState;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for saving actions in master server to pass them back to slave server.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/actions")
@ConditionalOnBean(MasterConfig.class)
public class ActionMasterController {

    private final MasterState masterState;

    private final ActionService actionService;

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public List<ActionDefinitionDto> getActions() {
        return actionService.getActions();
    }

    @GetMapping("/pending")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public List<ActionDto> getPendingActions() {
        return masterState.getActions();
    }

    @PostMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public void runAction(@Valid @RequestBody ActionDto actionDto) {
        actionService.runAction(actionDto);
    }

}
