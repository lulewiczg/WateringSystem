package com.github.lulewiczg.watering.state;

import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.config.dto.TankConfig;
import com.github.lulewiczg.watering.config.dto.TankType;
import com.github.lulewiczg.watering.config.dto.ValveConfig;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.github.lulewiczg.watering.state.mapper.TankConfigToTankMapper;
import com.github.lulewiczg.watering.state.mapper.ValveConfigToValveMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    private List<Tank> taps;

    private List<Valve> outputs;

    public AppState(AppConfig config, ValveConfigToValveMapper valveMapper, TankConfigToTankMapper tankMapper) {
        List<TankConfig> tanks = config.getTanks().values().stream()
                .filter(i -> i.getType() == TankType.DEFAULT).collect(Collectors.toList());
        this.tanks = tankMapper.map(tanks);

        List<TankConfig> taps = config.getTanks().values().stream()
                .filter(i -> i.getType() == TankType.UNLIMITED).collect(Collectors.toList());
        this.taps = tankMapper.map(taps);

        List<ValveConfig> outputs = config.getValves().values().stream()
                .filter(i -> i.getType() == ValveType.OUTPUT).collect(Collectors.toList());
        this.outputs = valveMapper.map(outputs);
    }
}
