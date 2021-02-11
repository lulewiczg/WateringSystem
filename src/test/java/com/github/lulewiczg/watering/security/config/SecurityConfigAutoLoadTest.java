package com.github.lulewiczg.watering.security.config;

import com.github.lulewiczg.watering.security.Role;
import com.github.lulewiczg.watering.security.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class SecurityConfigAutoLoadTest {

    @Autowired
    private SecurityConfig config;

    @Test
    void testPropsLoad() {
        User user = new User("admin", "$2y$12$u4p5J2U.UByIpLFNglfDHevj6TisurCEvBKYu3fhBbawvKHwyv2J6", List.of(Role.ADMIN), List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        User user2 = new User("user", "$2y$12$u4p5J2U.UByIpLFNglfDHevj6TisurCEvBKYu3fhBbawvKHwyv2J6", List.of(Role.USER), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        User user3 = new User("guest", "$2y$12$u4p5J2U.UByIpLFNglfDHevj6TisurCEvBKYu3fhBbawvKHwyv2J6", List.of(Role.GUEST), List.of(new SimpleGrantedAuthority("ROLE_GUEST")));
        User user4 = new User("slave", "$2y$12$u4p5J2U.UByIpLFNglfDHevj6TisurCEvBKYu3fhBbawvKHwyv2J6", List.of(Role.SLAVE), List.of(new SimpleGrantedAuthority("ROLE_SLAVE")));

        List<User> users = config.getUsers();
        assertEquals(List.of(user, user2, user3, user4), users);
    }

}
