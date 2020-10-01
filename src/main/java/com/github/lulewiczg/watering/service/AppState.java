package com.github.lulewiczg.watering.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.state.Tank;
import com.github.lulewiczg.watering.state.Valve;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean for holding application state.
 */
@Data
@Service
public class AppState {

    @JsonIgnore
    private final AppConfig config;

    private List<Tank> tanks;

    private List<Valve> outputValves;

    public AppState(AppConfig config) {
        this.config = config;
        tanks = config.getTanks().values().stream().map(Tank::new).collect(Collectors.toList());
        outputValves = config.getValves().values().stream().filter(i -> i.getType() == ValveType.OUTPUT).map(Valve::new).collect(Collectors.toList());
    }
}
