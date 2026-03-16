package com.github.lulewiczg.watering.config;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
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
    public Context pi4jContext() {
        return Pi4J.newAutoContext();
    }
}
