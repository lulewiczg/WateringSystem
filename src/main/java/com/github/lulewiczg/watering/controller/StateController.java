package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.state.AppState;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for obtaining current state.
 */
@RestController
@RequestMapping("/rest/state")
@RequiredArgsConstructor
public class StateController {

    private final AppState state;

    private final ApplicationContext ctx;

    @GetMapping
    @Secured({"ROLE_GUEST", "ROLE_USER", "ROLE_ADMIN"})
    public AppState getState() {
        return state;
    }

}
