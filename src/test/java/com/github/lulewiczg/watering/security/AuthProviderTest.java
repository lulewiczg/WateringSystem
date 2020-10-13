package com.github.lulewiczg.watering.security;

import com.github.lulewiczg.watering.security.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import(AuthProvider.class)
@ExtendWith(SpringExtension.class)
class AuthProviderTest {

    private static final String PWD = "$2y$04$k2006CnDU05nvhXVKBKiBOII8f47fzbV515ozlkgijic7nhXsGuxW";

    @MockBean
    private SecurityConfig securityConfig;

    @Autowired
    private AuthProvider authProvider;

    @Test
    void testLoginNotFound() {
        when(securityConfig.getUsers()).thenReturn(List.of(new User("test", "test", List.of(Role.ADMIN))));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test2", "test");

        String message = assertThrows(BadCredentialsException.class, () -> authProvider.authenticate(authentication)).getMessage();

        assertEquals("Invalid credentials", message);
    }

    @Test
    void testLoginInvalidPassword() {
        when(securityConfig.getUsers()).thenReturn(List.of(new User("test", "test", List.of(Role.ADMIN))));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test2", "abc");

        String message = assertThrows(BadCredentialsException.class, () -> authProvider.authenticate(authentication)).getMessage();

        assertEquals("Invalid credentials", message);
    }

    @Test
    void testLogin() {
        when(securityConfig.getUsers()).thenReturn(List.of(new User("name", PWD, List.of(Role.ADMIN)), new User("test2", "test2", List.of(Role.USER))));

        Authentication auth = authProvider.authenticate(new UsernamePasswordAuthenticationToken("name", "password"));

        assertEquals("name", auth.getName());
        assertEquals("password", auth.getCredentials());
        assertEquals(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")), auth.getAuthorities());
    }

}