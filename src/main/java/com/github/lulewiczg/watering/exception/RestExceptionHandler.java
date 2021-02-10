package com.github.lulewiczg.watering.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Rest API error handler.
 */
@Log4j2
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(getApiError(e, status, request), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handle(Exception e, WebRequest request) {
        return getGenericError(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handle(AccessDeniedException e, WebRequest request) {
        return getGenericError(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiError> handle(InsufficientAuthenticationException e, WebRequest request) {
        return getGenericError(e, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handle(BadCredentialsException e, WebRequest request) {
        return getGenericError(e, HttpStatus.FORBIDDEN, request);
    }

    private ResponseEntity<ApiError> getGenericError(Exception e, HttpStatus status, WebRequest request) {
        ApiError error = getApiError(e, status, request);
        return new ResponseEntity<>(error, status);
    }

    private ApiError getApiError(Exception e, HttpStatus status, WebRequest request) {
        log.error(e);
        log.error("Request info: {}", request.getDescription(true));
        return new ApiError(status.value(), status.getReasonPhrase(), e.getMessage());
    }
}
