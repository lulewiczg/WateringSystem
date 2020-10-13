package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ApiError;
import com.github.lulewiczg.watering.exception.RestExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(RestExceptionHandler.class)
class RestExceptionHandlerTest {

    @Autowired
    private RestExceptionHandler handler;

    @Mock
    private WebRequest req;

    @Test
    void testHandleUnexpected() {
        ApiError expected = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Some err");

        ResponseEntity<ApiError> response = handler.handle(new IllegalArgumentException("Some err"), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }

    @Test
    void testHandleAccessDenied() {
        ApiError expected = new ApiError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Some err");

        ResponseEntity<ApiError> response = handler.handle(new AccessDeniedException("Some err"), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }

    @Test
    void testHandleAuthException() {
        ApiError expected = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "Some err");

        ResponseEntity<ApiError> response = handler.handle(new InsufficientAuthenticationException("Some err"), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }

    @Test
    void testHandleBadCredentials() {
        ApiError expected = new ApiError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), "Some err");

        ResponseEntity<ApiError> response = handler.handle(new BadCredentialsException("Some err"), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }


}