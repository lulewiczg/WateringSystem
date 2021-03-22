package com.github.lulewiczg.watering.config;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.wiringpi.GpioUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class.
 */
@Configuration
public class Config {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(MasterConfig.class)
    @ConditionalOnExpression("!${com.github.lulewiczg.watering.mockedIO:false}")
    public GpioController gpioController() {
        GpioUtil.enableNonPrivilegedAccess();
        return GpioFactory.getInstance();
    }
}
