package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
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
@RequestMapping("/rest/actions")
public class ActionController {

    private final ActionService actionService;

    @GetMapping
    public List<ActionDefinitionDto> getActions() {
        return actionService.getActions();
    }

    @PostMapping
    public Object runAction(@Valid @RequestBody ActionDto actionDto) {
        return actionService.runAction(actionDto);
    }

}
