package com.github.lulewiczg.watering.config;

import com.github.lulewiczg.watering.config.dto.Valve;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.Size;
import java.util.Map;

@Data
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "config.valve")
public class ValveConfig {

    @Size(min = 1)
    private final Map<String, Valve> items;
}
