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
        AuthException authException = new AuthException(message, HttpStatus.UNAUTHORIZED);

        assertNotNull(authException);
        assertEquals(message, authException.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, authException.getStatus());
    }

    @Test
    void testAuthExceptionWithNullMessage() {
        String message = null;

        AuthException authException = new AuthException(message, HttpStatus.UNAUTHORIZED);

        assertNotNull(authException);
        assertNull(authException.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, authException.getStatus());
    }

    @Test
    void testAuthExceptionToString() {
        String message = "Auth exception toString test";
        AuthException authException = new AuthException(message, HttpStatus.UNAUTHORIZED);

        assertNotNull(authException);
        assertEquals(HttpStatus.UNAUTHORIZED, authException.getStatus());
        String toString = authException.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("AuthException"));
        assertTrue(toString.contains(message));
    }

    @Test
    void testAuthExceptionWithVeryLongMessage() {
        String message = "a".repeat(10000);

        AuthException authException = new AuthException(message, HttpStatus.UNAUTHORIZED);

        assertNotNull(authException);
        assertEquals(message, authException.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, authException.getStatus());
    }

    @Test
    void testAuthExceptionMultipleInstances() {
        String message1 = "First auth exception";
        String message2 = "Second auth exception";

        AuthException exception1 = new AuthException(message1, HttpStatus.UNAUTHORIZED);
        AuthException exception2 = new AuthException(message2, HttpStatus.BAD_REQUEST);

        assertNotNull(exception1);
        assertNotNull(exception2);
        assertEquals(message1, exception1.getMessage());
        assertEquals(message2, exception2.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception1.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, exception2.getStatus());
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
        assertNotEquals(exception1.getStatus(), exception2.getStatus());
    }

    @Test
    void testAuthExceptionWithCause() {
        String message = "Auth exception with cause";
        Exception cause = new RuntimeException("Root cause");
        AuthException authException = new AuthException(message, HttpStatus.UNAUTHORIZED);
        authException.initCause(cause);

        assertNotNull(authException);
        assertEquals(message, authException.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, authException.getStatus());
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
        AuthException authException = new AuthException(errorMessage, HttpStatus.UNAUTHORIZED);
        assertNotNull(authException);
        assertEquals(errorMessage, authException.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, authException.getStatus());
    }

    @Test
    void testAuthExceptionWithDifferentHttpStatuses() {
        AuthException unauthorizedException = new AuthException("Unauthorized", HttpStatus.UNAUTHORIZED);
        AuthException badRequestException = new AuthException("Bad request", HttpStatus.BAD_REQUEST);
        AuthException forbiddenException = new AuthException("Forbidden", HttpStatus.FORBIDDEN);

        assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedException.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, badRequestException.getStatus());
        assertEquals(HttpStatus.FORBIDDEN, forbiddenException.getStatus());
        
        assertEquals("Unauthorized", unauthorizedException.getMessage());
        assertEquals("Bad request", badRequestException.getMessage());
        assertEquals("Forbidden", forbiddenException.getMessage());
    }
}
