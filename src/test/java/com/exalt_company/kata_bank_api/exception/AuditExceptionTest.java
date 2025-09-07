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

class AuditExceptionTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "Audit operation failed",
            "",
            "This is a very long error message that contains multiple sentences and should be properly handled by the exception class. It should not cause any issues with the constructor or message handling.",
            "Error with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?",
            "Error with unicode: éèêëàâäôöùûüçñ",
            "   Audit exception with whitespace   ",
            "Audit exception\nwith newline\ncharacters",
            "Audit exception\twith tab\tcharacters",
            "Audit exception with numbers: 1234567890",
            "Audit exception with mixed content: 123 ABC !@# éèê 中文"
    })
    void testConstructor(String arg) {
        AuditException exception = new AuditException(arg, HttpStatus.BAD_REQUEST);

        assertNotNull(exception);
        assertEquals(arg, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testAuditExceptionToString() {
        String message = "Audit exception toString test";
        AuditException exception = new AuditException(message, HttpStatus.BAD_REQUEST);

        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        String toString = exception.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("AuditException"));
        assertTrue(toString.contains(message));
    }

    @Test
    void testAuditExceptionWithVeryLongMessage() {
        String message = "a".repeat(10000);

        AuditException exception = new AuditException(message, HttpStatus.BAD_REQUEST);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testConstructorWithNullMessage() {
        String errorMessage = null;

        AuditException exception = new AuditException(errorMessage, HttpStatus.BAD_REQUEST);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testMultipleInstances() {
        String message1 = "First error message";
        String message2 = "Second error message";

        AuditException exception1 = new AuditException(message1, HttpStatus.BAD_REQUEST);
        AuditException exception2 = new AuditException(message2, HttpStatus.FORBIDDEN);

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
    void testExceptionChaining() {
        String message = "Root cause";
        Exception cause = new RuntimeException("Underlying issue");

        AuditException exception = new AuditException(message, HttpStatus.BAD_REQUEST);
        exception.initCause(cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(cause, exception.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Audit operation failed: insufficient permissions",
            "Audit log creation failed",
            "Audit trail corrupted",
            "Audit operation timeout",
            "Audit database connection failed",
            "Audit operation not authorized",
            "Audit log entry validation failed",
            "Audit operation rollback failed",
            "Audit trail integrity check failed",
            "Audit operation serialization failed"
    })
    void testAuditExceptionRealisticScenarios(String errorMessage) {
        AuditException exception = new AuditException(errorMessage, HttpStatus.BAD_REQUEST);
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testAuditExceptionWithDifferentHttpStatuses() {
        AuditException badRequestException = new AuditException("Bad request", HttpStatus.BAD_REQUEST);
        AuditException forbiddenException = new AuditException("Forbidden", HttpStatus.FORBIDDEN);
        AuditException internalServerErrorException = new AuditException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);

        assertEquals(HttpStatus.BAD_REQUEST, badRequestException.getStatus());
        assertEquals(HttpStatus.FORBIDDEN, forbiddenException.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, internalServerErrorException.getStatus());
        
        assertEquals("Bad request", badRequestException.getMessage());
        assertEquals("Forbidden", forbiddenException.getMessage());
        assertEquals("Internal error", internalServerErrorException.getMessage());
    }
}
