package com.github.lulewiczg.watering.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.verify;

@Import(AuthEntryPoint.class)
@ExtendWith(SpringExtension.class)
class AuthEntryPointTest {

    @Autowired
    private AuthEntryPoint entryPoint;

    @MockBean
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