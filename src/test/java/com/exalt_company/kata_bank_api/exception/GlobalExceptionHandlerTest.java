package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private ServletWebRequest webRequest;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        webRequest = new ServletWebRequest(request);
    }

    @Test
    void testHandleAuthException() {
        AuthException authException = new AuthException("Authentication failed");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthException(authException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Authentication failed", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleBaseException() {
        BaseException baseException = new BaseException("Resource not found");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBaseException(baseException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("Resource not found", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleFundException() {
        FundException fundException = new FundException("Insufficient funds");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleFundException(fundException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Insufficient funds", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleGlobalException() {
        Exception exception = new RuntimeException("Unexpected error occurred");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Internal Server Error", response.getBody().error());
        assertEquals("Unexpected error occurred", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleAuthExceptionWithNullMessage() {
        AuthException authException = new AuthException(null);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthException(authException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertNull(response.getBody().message());
    }

    @Test
    void testHandleBaseExceptionWithEmptyMessage() {
        BaseException baseException = new BaseException("");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBaseException(baseException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("", response.getBody().message());
    }

    @Test
    void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON format");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadableException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Invalid JSON format", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }
}
