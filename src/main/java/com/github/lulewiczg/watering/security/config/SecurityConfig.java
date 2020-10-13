package com.github.lulewiczg.watering.security.config;

import com.github.lulewiczg.watering.security.Role;
import com.github.lulewiczg.watering.security.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Bean for holding system configuration.
 */
@Data
@Log4j2
@Validated
@RequiredArgsConstructor
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "config.security")
public class SecurityConfig {

    @Valid
    @NotEmpty
    private final List<User> users;

    @PostConstruct
    void postConstruct() {
        log.info("Found users: {}", users);
        if (users.stream().filter(i -> i.getRoles().contains(Role.ADMIN)).findAny().isEmpty()) {
            log.warn("Admin user is not set!");
        }
        validate();
    }

    void validate() {
        long uniqueUsers = users.stream().map(User::getName).distinct().count();
        if (users.size() != uniqueUsers) {
            throw new IllegalStateException("Duplicated users found!");
        }
    }

}
