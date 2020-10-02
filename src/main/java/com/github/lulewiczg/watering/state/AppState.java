package com.github.lulewiczg.watering.state;

import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.config.dto.TankType;
import com.github.lulewiczg.watering.config.dto.ValveConfig;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.github.lulewiczg.watering.state.dto.WaterSource;
import com.github.lulewiczg.watering.state.mapper.TankMapper;
import com.github.lulewiczg.watering.state.mapper.ValveMapper;
import com.github.lulewiczg.watering.state.mapper.WaterSourceMapper;
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

    private List<Tank> tanks;

    private List<WaterSource> taps;

    private List<Valve> outputs;

    public AppState(AppConfig config, ValveMapper valveMapper, TankMapper tankMapper, WaterSourceMapper waterSourceMapper) {
        List<TankConfig> tanks = config.getTanks().values().stream()
                .filter(i -> i.getType() == TankType.DEFAULT).collect(Collectors.toList());
        this.tanks = tankMapper.map(tanks);

        List<TankConfig> taps = config.getTanks().values().stream()
                .filter(i -> i.getType() == TankType.UNLIMITED).collect(Collectors.toList());
        this.taps = waterSourceMapper.map(taps);

        List<ValveConfig> outputs = config.getValves().values().stream()
                .filter(i -> i.getType() == ValveType.OUTPUT).collect(Collectors.toList());
        this.outputs = valveMapper.map(outputs);
    }
}
