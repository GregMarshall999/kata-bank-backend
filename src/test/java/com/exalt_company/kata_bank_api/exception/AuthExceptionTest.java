package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthExceptionTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "Authentication failed",
            "",
            "This is a very long authentication error message that contains detailed information about what went wrong during the authentication process. It should be properly handled and displayed to the user.",
            "Auth error with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?",
            "Auth error with unicode: éèêëàâäôöùûüçñ",
            "   Auth exception with whitespace   ",
            "Auth exception\nwith newline\ncharacters",
            "Auth exception\twith tab\tcharacters",
            "Auth exception with numbers: 1234567890",
            "Auth exception with mixed content: 123 ABC !@# éèê 中文"
    })
    void testConstructor(String message) {
        AuthException authException = new AuthException(message);

        assertNotNull(authException);
        assertEquals(message, authException.getMessage());
    }

    @Test
    void testAuthExceptionWithNullMessage() {
        String message = null;

        AuthException authException = new AuthException(message);

        assertNotNull(authException);
        assertNull(authException.getMessage());
    }

    @Test
    void testAuthExceptionToString() {
        String message = "Auth exception toString test";
        AuthException authException = new AuthException(message);

        assertNotNull(authException);
        String toString = authException.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("AuthException"));
        assertTrue(toString.contains(message));
    }

    @Test
    void testAuthExceptionWithVeryLongMessage() {
        String message = "a".repeat(10000);

        AuthException authException = new AuthException(message);

        assertNotNull(authException);
        assertEquals(message, authException.getMessage());
    }

    @Test
    void testAuthExceptionMultipleInstances() {
        String message1 = "First auth exception";
        String message2 = "Second auth exception";

        AuthException exception1 = new AuthException(message1);
        AuthException exception2 = new AuthException(message2);

        assertNotNull(exception1);
        assertNotNull(exception2);
        assertEquals(message1, exception1.getMessage());
        assertEquals(message2, exception2.getMessage());
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    void testAuthExceptionWithCause() {
        String message = "Auth exception with cause";
        Exception cause = new RuntimeException("Root cause");
        AuthException authException = new AuthException(message);
        authException.initCause(cause);

        assertNotNull(authException);
        assertEquals(message, authException.getMessage());
        assertEquals(cause, authException.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Invalid username or password",
            "Account locked due to multiple failed attempts",
            "Session expired, please login again",
            "Access denied: insufficient permissions",
            "Authentication token is invalid or expired"
    })
    void testAuthExceptionRealisticScenarios(String errorMessage) {
        AuthException authException = new AuthException(errorMessage);
        assertNotNull(authException);
        assertEquals(errorMessage, authException.getMessage());
    }
}
