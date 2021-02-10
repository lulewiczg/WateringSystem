package com.github.lulewiczg.watering.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Config for master mode.
 */
@Configuration
@ConditionalOnProperty(name = "com.github.lulewiczg.watering.role", havingValue = "master")
public class MasterConfig {
}
