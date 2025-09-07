package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.mock.http.MockHttpInputMessage;

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
        AuthException authException = new AuthException("Authentication failed", HttpStatus.UNAUTHORIZED);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthException(authException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().status());
        assertEquals("Unauthorized", response.getBody().error());
        assertEquals("Authentication failed", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleBaseException() {
        BaseException baseException = new BaseException("Resource not found", HttpStatus.NOT_FOUND);

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
        FundException fundException = new FundException("Insufficient funds", HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleFundException(fundException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Insufficient funds: REFUSED", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleFundExceptionWithNullMessage() {
        FundException fundException = new FundException(null, HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleFundException(fundException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("null: REFUSED", response.getBody().message());
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
        AuthException authException = new AuthException(null, HttpStatus.UNAUTHORIZED);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthException(authException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().status());
        assertEquals("Unauthorized", response.getBody().error());
        assertNull(response.getBody().message());
    }

    @Test
    void testHandleBaseExceptionWithEmptyMessage() {
        BaseException baseException = new BaseException("", HttpStatus.NOT_FOUND);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBaseException(baseException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("", response.getBody().message());
    }

    @ParameterizedTest
    @CsvSource({
            "Insufficient savings balance, Insufficient savings balance: REFUSED",
            "Maximum balance exceeded, Maximum balance exceeded: REFUSED"
    })
    void testHandleSavingException(String savingExceptionString, String responseMessage) {
        SavingException savingException = new SavingException(savingExceptionString, HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleSavingException(savingException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals(responseMessage, response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleSavingExceptionWithNullMessage() {
        SavingException savingException = new SavingException(null, HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleSavingException(savingException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("null: REFUSED", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleHttpMessageNotReadableException() {
        HttpInputMessage httpInputMessage = new MockHttpInputMessage(new byte[0]);
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON format", httpInputMessage);

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

    @Test
    void testHandleAuthExceptionWithEmptyMessage() {
        AuthException authException = new AuthException("", HttpStatus.UNAUTHORIZED);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthException(authException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().status());
        assertEquals("Unauthorized", response.getBody().error());
        assertEquals("", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleGlobalExceptionWithEmptyMessage() {
        Exception exception = new RuntimeException("");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Internal Server Error", response.getBody().error());
        assertEquals("", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleHttpMessageNotReadableExceptionWithNullMessage() {
        HttpInputMessage httpInputMessage = new MockHttpInputMessage(new byte[0]);
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("", httpInputMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadableException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleBaseExceptionWithLongMessage() {
        String longMessage = "This is a very long base exception message that contains detailed information about what went wrong during the base functionality processing. It should be properly handled and displayed to the user.";
        BaseException baseException = new BaseException(longMessage, HttpStatus.NOT_FOUND);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBaseException(baseException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals(longMessage, response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleAuthExceptionWithSpecialCharacters() {
        String messageWithSpecialChars = "Authentication failed: Invalid credentials! @#$%^&*()";
        AuthException authException = new AuthException(messageWithSpecialChars, HttpStatus.UNAUTHORIZED);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthException(authException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().status());
        assertEquals("Unauthorized", response.getBody().error());
        assertEquals(messageWithSpecialChars, response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleAuthExceptionWithBadRequestStatus() {
        AuthException authException = new AuthException("Bad request", HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthException(authException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Bad request", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleFundExceptionWithUnicodeCharacters() {
        String messageWithUnicode = "Insufficient funds: Montant insuffisant";
        FundException fundException = new FundException(messageWithUnicode, HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleFundException(fundException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals(messageWithUnicode + ": REFUSED", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleAuditException() {
        AuditException auditException = new AuditException("Audit operation failed", HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuditException(auditException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("Audit operation failed", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleAuditExceptionWithNullMessage() {
        AuditException auditException = new AuditException(null, HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuditException(auditException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertNull(response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleAuditExceptionWithEmptyMessage() {
        AuditException auditException = new AuditException("", HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuditException(auditException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("", response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @ParameterizedTest
    @CsvSource({
            "Invalid account type for audit, Invalid account type for audit",
            "User not found for audit operation, User not found for audit operation",
            "Audit record creation failed, Audit record creation failed"
    })
    void testHandleAuditExceptionWithDifferentMessages(String auditExceptionMessage, String expectedResponseMessage) {
        AuditException auditException = new AuditException(auditExceptionMessage, HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuditException(auditException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals(expectedResponseMessage, response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleAuditExceptionWithLongMessage() {
        String longMessage = "This is a very long audit exception message that contains detailed information about what went wrong during the audit operation processing. It should be properly handled and displayed to the user without any truncation or modification.";
        AuditException auditException = new AuditException(longMessage, HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuditException(auditException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals(longMessage, response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleAuditExceptionWithSpecialCharacters() {
        String messageWithSpecialChars = "Audit failed: Invalid operation! @#$%^&*()";
        AuditException auditException = new AuditException(messageWithSpecialChars, HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuditException(auditException, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals(messageWithSpecialChars, response.getBody().message());
        assertEquals("uri=/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }
}
