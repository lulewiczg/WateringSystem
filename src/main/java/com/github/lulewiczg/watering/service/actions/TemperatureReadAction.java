package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;

/**
 * Action for closing tanks.
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(MasterConfig.class)
public class TemperatureReadAction extends Action<Void, BigDecimal> {

    private final AppState state;

    @Value("${com.github.lulewiczg.watering.temperatureRead.command}")
    private String command;

    @SneakyThrows
    @Override
    protected BigDecimal doAction(ActionDto actionDto, Void param) {
        log.debug("Reading temperature...");
        Process exec = Runtime.getRuntime().exec(command.split(";"));
        BigDecimal temp = new BigDecimal(StreamUtils.copyToString(exec.getInputStream(), Charset.defaultCharset()).trim()).setScale(1, RoundingMode.HALF_UP);
        state.setTemperature(temp);
        log.debug("Temperature: {}", temp);
        return temp;
    }

    @Override
    public Class<?> getReturnType() {
        return BigDecimal.class;
    }

    @Override
    public String getDescription() {
        return "Reads system temperature";
    }
}
