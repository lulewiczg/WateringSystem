package com.github.lulewiczg.watering;

import com.github.lulewiczg.watering.config.ValveConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ValveConfig.class)
public class WateringApplication implements CommandLineRunner {

    @Autowired
    private ValveConfig config;

    public static void main(String[] args) {
        SpringApplication.run(WateringApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Ready");
    }
}
