package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Action for shutting down whole system.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class ShutdownAction extends Action<Void, Void> {

    @Value("${com.github.lulewiczg.watering.shutdownEnabled:false}")
    private boolean enabled;

    @Value("${com.github.lulewiczg.watering.shutdown.command}")
    private String command;

    @SneakyThrows
    @Override
    protected Void doAction(ActionDto actionDto, Void param) {
        log.info("Shutting down!");
        Runtime.getRuntime().exec(command.split(";"));
        new Thread(this::exit).start();
        return null;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getDescription() {
        return "Shuts down system";
    }

    void exit() {
        System.exit(101);
    }
}
