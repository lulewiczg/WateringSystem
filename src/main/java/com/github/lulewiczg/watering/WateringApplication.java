package com.github.lulewiczg.watering;

import com.github.lulewiczg.watering.config.AppConfig;
import com.github.lulewiczg.watering.security.config.SecurityConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Watering application.
 */
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({AppConfig.class, SecurityConfig.class})
public class WateringApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WateringApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Do nothing
    }
}
