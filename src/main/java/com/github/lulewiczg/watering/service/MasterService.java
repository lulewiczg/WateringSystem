package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.SlaveStateDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.MasterState;
import com.github.lulewiczg.watering.state.dto.MasterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * Service for master server.
 */
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
        AppState appState = state.getState();
        this.state.setState(appState.getState());
        this.state.setOutputs(appState.getOutputs());
        this.state.setTanks(appState.getTanks());
        this.state.setTaps(appState.getTaps());
        this.masterState.setActionDefinitions(state.getActions());
        this.masterState.setJobDefinitions(state.getJobs());
        MasterResponse masterResponse = new MasterResponse(masterState.getActions(), masterState.getJobs());
        masterState.getActions().clear();
        masterResponse.getJobs().clear();
        return masterResponse;
    }
}
