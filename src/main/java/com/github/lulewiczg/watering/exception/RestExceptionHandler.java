package com.github.lulewiczg.watering.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Rest API error handler.
 */
@Log4j2
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handle(Exception e) {
        return getGenericError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handle(AccessDeniedException e) {
        return getGenericError(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiError> handle(InsufficientAuthenticationException e) {
        return getGenericError(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handle(BadCredentialsException e) {
        return getGenericError(e, HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<ApiError> getGenericError(Exception e, HttpStatus status) {
        log.error(e);
        ApiError error = new ApiError(status.value(), status.getReasonPhrase(), e.getMessage());
        return new ResponseEntity<>(error, status);
    }
}
