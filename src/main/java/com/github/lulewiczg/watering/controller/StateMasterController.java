package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.MasterState;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for updating app state on master app.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/state")
@ConditionalOnProperty(name = "com.github.lulewiczg.watering.role", havingValue = "master")
public class StateMasterController {

    private final AppState state;

    private final MasterState masterState;

    @PostMapping
    @PreAuthorize(value = "hasAuthority('ROLE_USER') and authentication.principal.equals('slave') ")
    public MasterState updateState(@RequestBody AppState state) {
        this.state.setState(state.getState());
        this.state.setOutputs(state.getOutputs());
        this.state.setTanks(state.getTanks());
        this.state.setTaps(state.getTaps());
        return masterState;
    }
}
