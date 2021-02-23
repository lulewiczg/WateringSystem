package com.github.lulewiczg.watering.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authentication entry point for app.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class AuthEntryPoint extends BasicAuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, String.format("Basic realm=\"%s\"", getRealmName()));
        handlerExceptionResolver.resolveException(request, response, null, authException);
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("Watering");
        super.afterPropertiesSet();
    }
}