package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.*;
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

import java.util.List;
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
        ApiError expected = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Some err");

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

    @Test
    void testHandleActionNotFound() {
        ApiError expected = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Action not found: Some err");

        ResponseEntity<ApiError> response = handler.handle(new ActionNotFoundException("Some err"), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }

    @Test
    void testHandleActionError() {
        ApiError expected = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Action [id] failed: Some err");

        ResponseEntity<ApiError> response = handler.handle(new ActionException("id", "Some err"), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }

    @Test
    void testHandleValueNotAllowed() {
        ApiError expected = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Value [Some err] does not match [1, 2]!");

        ResponseEntity<ApiError> response = handler.handle(new ValueNotAllowedException("Some err", List.of("1", "2")), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }

    @Test
    void testHandleActionNotStarted() {
        ApiError expected = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Action [Some err] can not be started!");

        ResponseEntity<ApiError> response = handler.handle(new ActionNotStartedException("Some err"), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }

    @Test
    void testHandleJobNotFound() {
        ApiError expected = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Job not found: Some err");

        ResponseEntity<ApiError> response = handler.handle(new JobNotFoundException("Some err"), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }

    @Test
    void testHandleValveNotFound() {
        ApiError expected = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Valve not found: Some err");

        ResponseEntity<ApiError> response = handler.handle(new ValveNotFoundException("Some err"), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }

    @Test
    void testHandleTypeMismatch() {
        ApiError expected = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "[Some err] is not valid value for class java.lang.Double type!");

        ResponseEntity<ApiError> response = handler.handle(new TypeMismatchException("Some err", Double.class), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }

    @Test
    void testHandleSensorNotFound() {
        ApiError expected = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Sensor not found: Some err");

        ResponseEntity<ApiError> response = handler.handle(new SensorNotFoundException("Some err"), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }

    @Test
    void testHandleValidationError() {
        ApiError expected = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Error");

        ResponseEntity<ApiError> response = handler.handle(new ValidationException("Error"), req);

        TestUtils.testError(Objects.requireNonNull(response.getBody()), expected);
    }
}
