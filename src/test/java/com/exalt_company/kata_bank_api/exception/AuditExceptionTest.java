package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuditExceptionTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "Audit operation failed",
            "",
            "This is a very long error message that contains multiple sentences and should be properly handled by the exception class. It should not cause any issues with the constructor or message handling.",
            "Error with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?",
            "Error with unicode: éèêëàâäôöùûüçñ"
    })
    void testConstructor(String arg) {
        AuditException exception = new AuditException(arg);

        assertNotNull(exception);
        assertEquals(arg, exception.getMessage());
    }

    @Test
    void testConstructorWithNullMessage() {
        String errorMessage = null;

        AuditException exception = new AuditException(errorMessage);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testMultipleInstances() {
        String message1 = "First error message";
        String message2 = "Second error message";

        AuditException exception1 = new AuditException(message1);
        AuditException exception2 = new AuditException(message2);

        assertNotNull(exception1);
        assertNotNull(exception2);
        assertEquals(message1, exception1.getMessage());
        assertEquals(message2, exception2.getMessage());
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    void testExceptionChaining() {
        String message = "Root cause";
        Exception cause = new RuntimeException("Underlying issue");

        AuditException exception = new AuditException(message);
        exception.initCause(cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
