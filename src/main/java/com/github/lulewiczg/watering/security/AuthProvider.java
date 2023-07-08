package com.github.lulewiczg.watering.security;

import com.github.lulewiczg.watering.security.config.SecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * Authentication provider for application.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class AuthProvider implements AuthenticationProvider {

    private final SecurityConfig securityConfig;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        log.debug("Trying to log in with user: {}", name);

        User user = securityConfig.getUsers().stream().filter(i -> i.getName().equals(name)).findFirst()
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        checkPassword(password, user);
        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private void checkPassword(String password, User user) {
        try {
            if (!BCrypt.checkpw(password, user.getPassword())) {
                throwError(password, user);
            }
        } catch (Exception e) {
            log.debug(e);
            throwError(password, user);
        }
    }

    private void throwError(String password, User user) {
        log.error("Attempting to log in as user {} with password {}", user, password);
        throw new BadCredentialsException("Invalid credentials");
    }

}
