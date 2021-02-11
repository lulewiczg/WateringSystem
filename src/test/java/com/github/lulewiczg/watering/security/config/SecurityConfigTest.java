package com.github.lulewiczg.watering.security.config;

import com.github.lulewiczg.watering.security.Role;
import com.github.lulewiczg.watering.security.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Import(LocalValidatorFactoryBean.class)
class SecurityConfigTest {

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    void tetNoUsers() {
        SecurityConfig config = new SecurityConfig(List.of());

        testValidate(config, "users must not be empty");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testData/users-test.csv")
    void testLoadNoUsers(String name, String password, String rolesStr, String error) {
        List<Role> roles;
        if (rolesStr == null || rolesStr.isEmpty()) {
            roles = new ArrayList<>();
        } else {
            roles = Arrays.stream(rolesStr.split("\\|")).map(Role::valueOf).collect(Collectors.toList());
        }
        User user = new User("user", "test", List.of(Role.ADMIN));
        User user2 = new User(name, password, roles);

        SecurityConfig config = new SecurityConfig(List.of(user, user2));

        testValidate(config, error);
    }

    private void testValidate(SecurityConfig config, String message) {
        ConstraintViolation<SecurityConfig> error = validateFields(config);
        if (error != null) {
            assertEquals(error.getPropertyPath() + " " + error.getMessage(), message);
        } else {
            if (message != null) {
                String msg = assertThrows(IllegalStateException.class, config::validate).getMessage();
                assertEquals(msg, message);
            } else {
                assertDoesNotThrow(config::validate);
            }
        }
    }

    private ConstraintViolation<SecurityConfig> validateFields(SecurityConfig config) {
        Set<ConstraintViolation<SecurityConfig>> errors = validator.validate(config);
        Optional<ConstraintViolation<SecurityConfig>> error = errors.stream().min(Comparator.comparing(i -> i.getPropertyPath().toString()));
        return error.orElse(null);
    }

}
