package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthExceptionTest {
    @Test
    void testAuthExceptionWithMessage() {
        String message = "Authentication failed";

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
    void testAuthExceptionWithEmptyMessage() {
        String message = "";

        AuthException authException = new AuthException(message);

        assertNotNull(authException);
        assertEquals("", authException.getMessage());
    }

    @Test
    void testAuthExceptionWithLongMessage() {
        String message = "This is a very long authentication error message that contains detailed information about what went wrong during the authentication process. It should be properly handled and displayed to the user.";

        AuthException authException = new AuthException(message);

        assertNotNull(authException);
        assertEquals(message, authException.getMessage());
    }
}
