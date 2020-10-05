package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Valve;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Job for checking valves state.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.sensorsRead.enabled")
public class ScheduledValveRead {

    private final AppState state;

    private final IOService ioService;

    /**
     * Runs action.
     */
    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.sensorsRead.cron}")
    public void run() {
        log.info("Checking valves...");
        state.getTanks().forEach(i -> {
            Valve valve = i.getValve();
            boolean result = ioService.readPin(valve.getPin());
            if (valve.isOpen() != result) {
                log.error("Invalid state for valve {}, should be {}", valve.getId(), valve.isOpen());
                state.setState(SystemStatus.ERROR);
            }
        });
        log.info("Valves check finished.");
    }

}
