package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Controller for obtaining current state.
 */
@Log4j2
@Generated
@Profile("test")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestStateController {

    private final AppState state;

    @GetMapping("/state")
    public AppState getState() {
        return state;
    }

    @PostMapping("/sensor/{id}")
    public void setSensor(@PathVariable("id") @NotNull String id, @RequestBody Sensor sensor) {
        log.info("Setting sensor {}", sensor);
        Sensor old = this.state.getTanks().stream().map(Tank::getSensor).filter(i -> i.getId().equals(id)).findFirst().orElseThrow();
        old.setLevel(sensor.getLevel());
        old.setMaxLevel(sensor.getMaxLevel());
        old.setMinLevel(sensor.getMinLevel());
    }

    @PostMapping("/tank/{id}")
    public void setTank(@PathVariable("id") @NotNull String id, @RequestBody Tank tank) {
        log.info("Setting tank {}", tank);
        Tank old = this.state.getTanks().stream().filter(i -> i.getId().equals(id)).findFirst().orElseThrow();
        old.setVolume(tank.getVolume());
    }

    @PostMapping("/valve/{id}")
    public void setValve(@PathVariable("id") @NotNull String id, @RequestBody Valve valve) {
        log.info("Setting valve {}", valve);
        Optional<Valve> old = this.state.getTanks().stream().map(Tank::getValve).filter(i -> i.getId().equals(id)).findFirst();
        Optional<Valve> old2 = this.state.getOutputs().stream().filter(i -> i.getId().equals(id)).findFirst();
        Valve oldValve = old.orElse(old2.orElseThrow());

        oldValve.setOpen(valve.isOpen());
        oldValve.setName(valve.getName());
        oldValve.setType(valve.getType());
    }
}
