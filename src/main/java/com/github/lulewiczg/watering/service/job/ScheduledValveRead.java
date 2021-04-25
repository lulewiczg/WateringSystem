package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.github.lulewiczg.watering.state.dto.WaterSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Job for checking valves state.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
@ConditionalOnProperty("com.github.lulewiczg.watering.schedule.valveRead.enabled")
public class ScheduledValveRead extends ScheduledIoJob {

    private final AppState state;

    private final IOService ioService;

    private final JobRunner jobRunner;

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.valveRead.cron}")
    void schedule() {
        schedule(jobRunner);
    }

    @Override
    public String getName() {
        return "Valves state read";
    }

    @Override
    public void doJob(JobDto job) {
        log.debug("Checking valves...");
        state.getOutputs().forEach(this::readValve);
        state.getTaps().stream().map(WaterSource::getValve).forEach(this::readValve);
        state.getTanks().stream().map(Tank::getValve).forEach(this::readValve);
        log.debug("Valves check finished.");
    }

    private void readValve(Valve i) {
        boolean result = ioService.readPin(i.getPin());
        if (i.isOpen() != result) {
            log.error("Invalid state for valve {}, should be {}", i.getId(), i.isOpen());
            state.setState(SystemStatus.ERROR);
        }
    }

}
