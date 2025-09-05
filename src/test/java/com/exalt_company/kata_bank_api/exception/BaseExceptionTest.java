package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseExceptionTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "Resource not found",
            "",
            "This is a very long base error message that contains detailed information about what went wrong during the base functionality processing. It should be properly handled and displayed to the user.",
            "Base error with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?",
            "Base error with unicode: éèêëàâäôöùûüçñ",
            "   Base exception with whitespace   ",
            "Base exception\nwith newline\ncharacters",
            "Base exception\twith tab\tcharacters",
            "Base exception with numbers: 1234567890",
            "Base exception with mixed content: 123 ABC !@# éèê 中文"
    })
    void testConstructor(String message) {
        BaseException baseException = new BaseException(message);

        assertNotNull(baseException);
        assertEquals(message, baseException.getMessage());
    }

    @Test
    void testBaseExceptionWithNullMessage() {
        String message = null;

        BaseException baseException = new BaseException(message);

        assertNotNull(baseException);
        assertNull(baseException.getMessage());
    }

    @Test
    void testBaseExceptionToString() {
        String message = "Base exception toString test";
        BaseException baseException = new BaseException(message);

        assertNotNull(baseException);
        String toString = baseException.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("BaseException"));
        assertTrue(toString.contains(message));
    }

    @Test
    void testBaseExceptionWithVeryLongMessage() {
        String message = "a".repeat(10000);

        BaseException baseException = new BaseException(message);

        assertNotNull(baseException);
        assertEquals(message, baseException.getMessage());
    }

    @Test
    void testBaseExceptionMultipleInstances() {
        String message1 = "First base exception";
        String message2 = "Second base exception";

        BaseException exception1 = new BaseException(message1);
        BaseException exception2 = new BaseException(message2);

        assertNotNull(exception1);
        assertNotNull(exception2);
        assertEquals(message1, exception1.getMessage());
        assertEquals(message2, exception2.getMessage());
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    void testBaseExceptionWithCause() {
        String message = "Base exception with cause";
        Exception cause = new RuntimeException("Root cause");
        BaseException baseException = new BaseException(message);
        baseException.initCause(cause);

        assertNotNull(baseException);
        assertEquals(message, baseException.getMessage());
        assertEquals(cause, baseException.getCause());
    }
}
