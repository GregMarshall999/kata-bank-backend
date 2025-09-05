package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        SavingException savingException = new SavingException(message);

        assertNotNull(savingException);
        assertEquals(message, savingException.getMessage());
    }

    @Test
    void testSavingExceptionWithNullMessage() {
        String message = null;

        SavingException savingException = new SavingException(message);

        assertNotNull(savingException);
        assertNull(savingException.getMessage());
    }

    @Test
    void testSavingExceptionToString() {
        String message = "Saving exception toString test";
        SavingException savingException = new SavingException(message);

        assertNotNull(savingException);
        String toString = savingException.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("SavingException"));
        assertTrue(toString.contains(message));
    }

    @Test
    void testSavingExceptionWithVeryLongMessage() {
        String message = "a".repeat(10000);

        SavingException savingException = new SavingException(message);

        assertNotNull(savingException);
        assertEquals(message, savingException.getMessage());
    }

    @Test
    void testSavingExceptionMultipleInstances() {
        String message1 = "First saving exception";
        String message2 = "Second saving exception";

        SavingException exception1 = new SavingException(message1);
        SavingException exception2 = new SavingException(message2);

        assertNotNull(exception1);
        assertNotNull(exception2);
        assertEquals(message1, exception1.getMessage());
        assertEquals(message2, exception2.getMessage());
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    void testSavingExceptionWithCause() {
        String message = "Saving exception with cause";
        Exception cause = new RuntimeException("Root cause");
        SavingException savingException = new SavingException(message);
        savingException.initCause(cause);

        assertNotNull(savingException);
        assertEquals(message, savingException.getMessage());
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
        SavingException savingException = new SavingException(errorMessage);
        assertNotNull(savingException);
        assertEquals(errorMessage, savingException.getMessage());
    }
}
