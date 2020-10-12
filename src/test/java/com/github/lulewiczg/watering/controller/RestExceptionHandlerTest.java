package com.github.lulewiczg.watering.controller;

import com.github.lulewiczg.watering.exception.ApiError;
import com.github.lulewiczg.watering.exception.RestExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(RestExceptionHandler.class)
class RestExceptionHandlerTest {

    @Autowired
    private RestExceptionHandler handler;

    @Test
    void testHandle() {
        Date date = new Date();
        ResponseEntity<ApiError> response = handler.handle(new IllegalArgumentException("Some err"));
        ApiError expected = new ApiError(400, "Bad Request", "Some err");

        assertEquals(400, response.getStatusCode().value());
        ApiError error = response.getBody();
        assertNotNull(error);
        assertNotNull(error.getTimestamp());
        assertTrue(date.equals(error.getTimestamp()) || date.before(error.getTimestamp()));
        error.setTimestamp(expected.getTimestamp());
        assertEquals(expected, error);
    }

}