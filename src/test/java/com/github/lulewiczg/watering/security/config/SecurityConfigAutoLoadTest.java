package com.github.lulewiczg.watering.security.config;

import com.github.lulewiczg.watering.security.Role;
import com.github.lulewiczg.watering.security.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class SecurityConfigAutoLoadTest {

    @Autowired
    private SecurityConfig config;

    @Test
    void testPropsLoad() {
        User user = new User("admin", "$2y$12$u4p5J2U.UByIpLFNglfDHevj6TisurCEvBKYu3fhBbawvKHwyv2J6", List.of(Role.ADMIN));
        User user2 = new User("user", "$2y$12$u4p5J2U.UByIpLFNglfDHevj6TisurCEvBKYu3fhBbawvKHwyv2J6", List.of(Role.USER));
        User user3 = new User("guest", "$2y$12$u4p5J2U.UByIpLFNglfDHevj6TisurCEvBKYu3fhBbawvKHwyv2J6", List.of(Role.GUEST));

        List<User> configuredTanks = config.getUsers();
        assertEquals(List.of(user, user2, user3), configuredTanks);
    }

}