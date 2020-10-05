package com.github.lulewiczg.watering.state;

import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.config.dto.TankType;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.github.lulewiczg.watering.state.dto.WaterSource;
import com.github.lulewiczg.watering.state.mapper.TankMapper;
import com.github.lulewiczg.watering.state.mapper.ValveMapper;
import com.github.lulewiczg.watering.state.mapper.WaterSourceMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean for holding application state.
 */
@Data
@Service
@NoArgsConstructor
public class AppState {

    private List<Tank> tanks;

    private List<WaterSource> taps;

    private List<Valve> outputs;

    private SystemStatus state = SystemStatus.IDLE;

    @Autowired
    public AppState(AppConfig config, ValveMapper valveMapper, TankMapper tankMapper, WaterSourceMapper waterSourceMapper) {
        this.tanks = tankMapper.map(config.getTanks().stream()
                .filter(i1 -> i1.getType() == TankType.DEFAULT).collect(Collectors.toList()));

        this.taps = waterSourceMapper.map(config.getTanks().stream()
                .filter(i1 -> i1.getType() == TankType.UNLIMITED).collect(Collectors.toList()));

        this.outputs = valveMapper.map(config.getValves().stream()
                .filter(i -> i.getType() == ValveType.OUTPUT).collect(Collectors.toList()));
    }
}
