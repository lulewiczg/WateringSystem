package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.MasterService;
import com.github.lulewiczg.watering.service.dto.SlaveStateDto;
import com.github.lulewiczg.watering.state.dto.MasterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
@ConditionalOnBean(MasterConfig.class)
public class StateMasterController {

    private final MasterService service;

    @PostMapping
    @PreAuthorize(value = "hasAuthority('ROLE_USER') and authentication.principal.username.equals('slave')")
    public MasterResponse updateState(@RequestBody SlaveStateDto state, Authentication auth) {
        return service.update(state);
    }


}
