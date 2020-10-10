package com.github.lulewiczg.watering.state;

import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.config.dto.TankType;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.exception.SensorNotFoundException;
import com.github.lulewiczg.watering.exception.ValveNotFoundException;
import com.github.lulewiczg.watering.state.dto.Sensor;
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
import java.util.Optional;
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

    private volatile SystemStatus state = SystemStatus.IDLE;

    /**
     * Finds valve with given ID.
     *
     * @param id ID
     * @return valve
     */
    public Valve findValve(String id) {
        Optional<Valve> valve = outputs.stream().filter(i -> i.getId().equals(id)).findFirst()
                .or(() -> tanks.stream().map(Tank::getValve).filter(i -> i.getId().equals(id)).findFirst())
                .or(() -> taps.stream().map(WaterSource::getValve).filter(i -> i.getId().equals(id)).findFirst());
        return valve.orElseThrow(() -> new ValveNotFoundException(id));
    }

    /**
     * Finds senor with given ID.
     *
     * @param id ID
     * @return sensor
     */
    public Sensor findSensor(String id) {
        return tanks.stream().map(Tank::getSensor).filter(i -> i != null && i.getId().equals(id)).findFirst()
                .orElseThrow(() -> new SensorNotFoundException(id));
    }

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
