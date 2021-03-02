package com.github.lulewiczg.watering.config;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Config for slave mode.
 */
@Component
@ConditionalOnProperty(name = "com.github.lulewiczg.watering.role", havingValue = "slave")
public class SlaveConfig {

}
