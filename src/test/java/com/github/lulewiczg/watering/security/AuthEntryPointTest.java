package com.github.lulewiczg.watering.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.mockito.Mockito.verify;

@Import(AuthEntryPoint.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class AuthEntryPointTest {

    @Autowired
    private AuthEntryPoint entryPoint;

    @MockitoBean
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    void testCommence() {
        BadCredentialsException e = new BadCredentialsException("some error");

        entryPoint.commence(request, response, e);

        verify(response).addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Watering\"");
        verify(handlerExceptionResolver).resolveException(request, response, null, e);
    }
}