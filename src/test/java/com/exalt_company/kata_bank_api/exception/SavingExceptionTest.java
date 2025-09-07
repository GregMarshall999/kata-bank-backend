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

class SavingExceptionTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "Insufficient savings balance",
            "",
            "This is a very long saving error message that contains detailed information about what went wrong during the saving account processing. It should be properly handled and displayed to the user.",
            "Saving error with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?",
            "Saving error with unicode: éèêëàâäôöùûüçñ",
            "   Saving exception with whitespace   ",
            "Saving exception\nwith newline\ncharacters",
            "Saving exception\twith tab\tcharacters",
            "Saving exception with numbers: 1234567890",
            "Saving exception with mixed content: 123 ABC !@# éèê 中文"
    })
    void testConstructor(String message) {
        SavingException savingException = new SavingException(message, HttpStatus.BAD_REQUEST);

        assertNotNull(savingException);
        assertEquals(message, savingException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, savingException.getStatus());
    }

    @Test
    void testSavingExceptionWithNullMessage() {
        String message = null;

        SavingException savingException = new SavingException(message, HttpStatus.BAD_REQUEST);

        assertNotNull(savingException);
        assertNull(savingException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, savingException.getStatus());
    }

    @Test
    void testSavingExceptionToString() {
        String message = "Saving exception toString test";
        SavingException savingException = new SavingException(message, HttpStatus.BAD_REQUEST);

        assertNotNull(savingException);
        assertEquals(HttpStatus.BAD_REQUEST, savingException.getStatus());
        String toString = savingException.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("SavingException"));
        assertTrue(toString.contains(message));
    }

    @Test
    void testSavingExceptionWithVeryLongMessage() {
        String message = "a".repeat(10000);

        SavingException savingException = new SavingException(message, HttpStatus.BAD_REQUEST);

        assertNotNull(savingException);
        assertEquals(message, savingException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, savingException.getStatus());
    }

    @Test
    void testSavingExceptionMultipleInstances() {
        String message1 = "First saving exception";
        String message2 = "Second saving exception";

        SavingException exception1 = new SavingException(message1, HttpStatus.BAD_REQUEST);
        SavingException exception2 = new SavingException(message2, HttpStatus.FORBIDDEN);

        assertNotNull(exception1);
        assertNotNull(exception2);
        assertEquals(message1, exception1.getMessage());
        assertEquals(message2, exception2.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception1.getStatus());
        assertEquals(HttpStatus.FORBIDDEN, exception2.getStatus());
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
        assertNotEquals(exception1.getStatus(), exception2.getStatus());
    }

    @Test
    void testSavingExceptionWithCause() {
        String message = "Saving exception with cause";
        Exception cause = new RuntimeException("Root cause");
        SavingException savingException = new SavingException(message, HttpStatus.BAD_REQUEST);
        savingException.initCause(cause);

        assertNotNull(savingException);
        assertEquals(message, savingException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, savingException.getStatus());
        assertEquals(cause, savingException.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Insufficient savings balance for withdrawal",
            "Maximum savings balance exceeded",
            "Saving account not found",
            "Invalid saving operation",
            "Saving transfer failed due to insufficient balance",
            "Maximum balance limit exceeded",
            "Saving account is frozen",
            "Invalid saving amount specified",
            "Saving account has reached maximum balance",
            "Withdrawal not allowed from this saving account"
    })
    void testSavingExceptionRealisticScenarios(String errorMessage) {
        SavingException savingException = new SavingException(errorMessage, HttpStatus.BAD_REQUEST);
        assertNotNull(savingException);
        assertEquals(errorMessage, savingException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, savingException.getStatus());
    }

    @Test
    void testSavingExceptionWithDifferentHttpStatuses() {
        SavingException badRequestException = new SavingException("Bad request", HttpStatus.BAD_REQUEST);
        SavingException forbiddenException = new SavingException("Forbidden", HttpStatus.FORBIDDEN);
        SavingException conflictException = new SavingException("Conflict", HttpStatus.CONFLICT);

        assertEquals(HttpStatus.BAD_REQUEST, badRequestException.getStatus());
        assertEquals(HttpStatus.FORBIDDEN, forbiddenException.getStatus());
        assertEquals(HttpStatus.CONFLICT, conflictException.getStatus());
        
        assertEquals("Bad request", badRequestException.getMessage());
        assertEquals("Forbidden", forbiddenException.getMessage());
        assertEquals("Conflict", conflictException.getMessage());
    }
}
