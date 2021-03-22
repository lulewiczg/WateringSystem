package com.github.lulewiczg.watering.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Config for slave mode.
 */
@Component
@ConditionalOnProperty(name = "com.github.lulewiczg.watering.role", havingValue = "slave")
public class SlaveConfig {

}
