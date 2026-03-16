package com.github.lulewiczg.watering.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Authentication entry point for app.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class AuthEntryPoint extends BasicAuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull AuthenticationException authException) {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, String.format("Basic realm=\"%s\"", getRealmName()));
        handlerExceptionResolver.resolveException(request, response, null, authException);
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("Watering");
        super.afterPropertiesSet();
    }
}