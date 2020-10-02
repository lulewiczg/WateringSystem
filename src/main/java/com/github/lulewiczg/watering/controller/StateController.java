package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.state.AppState;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for obtaining current state.
 */
@RestController
@RequestMapping("/state")
@RequiredArgsConstructor
public class StateController {

    private final AppState state;

    @GetMapping
    public AppState getState() {
        return state;
    }
}
