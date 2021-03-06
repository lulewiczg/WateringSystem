package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for actions.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/actions")
@ConditionalOnMissingBean(MasterConfig.class)
public class ActionController {

    private final ActionService actionService;

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public List<ActionDefinitionDto> getActions() {
        return actionService.getActions();
    }

    @PostMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ActionResultDto<?> runAction(@Valid @RequestBody ActionDto actionDto) {
        return actionService.runAction(actionDto);
    }

}
