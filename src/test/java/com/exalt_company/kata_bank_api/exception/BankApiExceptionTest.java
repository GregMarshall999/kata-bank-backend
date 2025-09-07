package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankApiExceptionTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "Bank API error occurred",
            "",
            "This is a very long bank API error message that contains detailed information about what went wrong during the bank API processing. It should be properly handled and displayed to the user.",
            "Bank API error with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?",
            "Bank API error with unicode: éèêëàâäôöùûüçñ",
            "   Bank API exception with whitespace   ",
            "Bank API exception\nwith newline\ncharacters",
            "Bank API exception\twith tab\tcharacters",
            "Bank API exception with numbers: 1234567890",
            "Bank API exception with mixed content: 123 ABC !@# éèê 中文"
    })
    void testConstructor(String message) {
        BankApiException bankApiException = new BankApiException(message, HttpStatus.INTERNAL_SERVER_ERROR);

        assertNotNull(bankApiException);
        assertEquals(message, bankApiException.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, bankApiException.getStatus());
    }

    @Test
    void testBankApiExceptionWithNullMessage() {
        String message = null;

        BankApiException bankApiException = new BankApiException(message, HttpStatus.INTERNAL_SERVER_ERROR);

        assertNotNull(bankApiException);
        assertNull(bankApiException.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, bankApiException.getStatus());
    }

    @Test
    void testBankApiExceptionToString() {
        String message = "Bank API exception toString test";
        BankApiException bankApiException = new BankApiException(message, HttpStatus.INTERNAL_SERVER_ERROR);

        assertNotNull(bankApiException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, bankApiException.getStatus());
        String toString = bankApiException.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("BankApiException"));
        assertTrue(toString.contains(message));
    }

    @Test
    void testBankApiExceptionWithVeryLongMessage() {
        String message = "a".repeat(10000);

        BankApiException bankApiException = new BankApiException(message, HttpStatus.INTERNAL_SERVER_ERROR);

        assertNotNull(bankApiException);
        assertEquals(message, bankApiException.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, bankApiException.getStatus());
    }

    @Test
    void testBankApiExceptionMultipleInstances() {
        String message1 = "First bank API exception";
        String message2 = "Second bank API exception";

        BankApiException exception1 = new BankApiException(message1, HttpStatus.INTERNAL_SERVER_ERROR);
        BankApiException exception2 = new BankApiException(message2, HttpStatus.BAD_REQUEST);

        assertNotNull(exception1);
        assertNotNull(exception2);
        assertEquals(message1, exception1.getMessage());
        assertEquals(message2, exception2.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception1.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, exception2.getStatus());
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
        assertNotEquals(exception1.getStatus(), exception2.getStatus());
    }

    @Test
    void testBankApiExceptionWithCause() {
        String message = "Bank API exception with cause";
        Exception cause = new RuntimeException("Root cause");
        BankApiException bankApiException = new BankApiException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        bankApiException.initCause(cause);

        assertNotNull(bankApiException);
        assertEquals(message, bankApiException.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, bankApiException.getStatus());
        assertEquals(cause, bankApiException.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Database connection failed",
            "External service unavailable",
            "Invalid request format",
            "Authentication token expired",
            "Rate limit exceeded",
            "Service temporarily unavailable",
            "Invalid configuration",
            "Network timeout occurred"
    })
    void testBankApiExceptionRealisticScenarios(String errorMessage) {
        BankApiException bankApiException = new BankApiException(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        assertNotNull(bankApiException);
        assertEquals(errorMessage, bankApiException.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, bankApiException.getStatus());
    }

    @Test
    void testBankApiExceptionWithDifferentHttpStatuses() {
        BankApiException badRequestException = new BankApiException("Bad request", HttpStatus.BAD_REQUEST);
        BankApiException unauthorizedException = new BankApiException("Unauthorized", HttpStatus.UNAUTHORIZED);
        BankApiException forbiddenException = new BankApiException("Forbidden", HttpStatus.FORBIDDEN);
        BankApiException notFoundException = new BankApiException("Not found", HttpStatus.NOT_FOUND);
        BankApiException internalServerErrorException = new BankApiException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);

        assertEquals(HttpStatus.BAD_REQUEST, badRequestException.getStatus());
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedException.getStatus());
        assertEquals(HttpStatus.FORBIDDEN, forbiddenException.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, notFoundException.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, internalServerErrorException.getStatus());
        
        assertEquals("Bad request", badRequestException.getMessage());
        assertEquals("Unauthorized", unauthorizedException.getMessage());
        assertEquals("Forbidden", forbiddenException.getMessage());
        assertEquals("Not found", notFoundException.getMessage());
        assertEquals("Internal error", internalServerErrorException.getMessage());
    }

    @Test
    void testBankApiExceptionInheritance() {
        BankApiException bankApiException = new BankApiException("Test message", HttpStatus.BAD_REQUEST);
        
        assertTrue(bankApiException instanceof Exception);
        
        assertEquals("Test message", bankApiException.getMessage());
        
        assertEquals(HttpStatus.BAD_REQUEST, bankApiException.getStatus());
    }
}
