package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.SlaveStateDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.MasterState;
import com.github.lulewiczg.watering.state.dto.MasterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service for master server.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnBean(MasterConfig.class)
public class MasterService {

    private final MasterState masterState;

    private final AppState state;

    /**
     * Updates state from slave and returns pending actions.
     *
     * @param state slave state
     * @return actions
     */
    public MasterResponse update(SlaveStateDto state) {
        log.debug("Updating slave state: {}", state);
        AppState appState = state.getState();
        this.state.setState(appState.getState());
        this.state.setOutputs(appState.getOutputs());
        this.state.setTanks(appState.getTanks());
        this.state.setTaps(appState.getTaps());
        this.masterState.setActionDefinitions(state.getActions());
        this.masterState.setJobDefinitions(state.getJobs());
        MasterResponse masterResponse = new MasterResponse(new ArrayList<>(masterState.getActions()), new ArrayList<>(masterState.getJobs()));
        masterState.getActions().clear();
        masterState.getJobs().clear();
        log.debug("Returning master state: {}", masterResponse);
        return masterResponse;
    }
}
