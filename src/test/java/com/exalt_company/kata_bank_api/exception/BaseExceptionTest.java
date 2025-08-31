package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BaseExceptionTest {

    @Test
    void testBaseExceptionWithMessage() {
        String message = "Resource not found";

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
    void testBaseExceptionWithEmptyMessage() {
        String message = "";

        BaseException baseException = new BaseException(message);

        assertNotNull(baseException);
        assertEquals("", baseException.getMessage());
    }

    @Test
    void testBaseExceptionWithLongMessage() {
        String message = "This is a very long base error message that contains detailed information about what went wrong during the base functionality processing. It should be properly handled and displayed to the user.";

        BaseException baseException = new BaseException(message);

        assertNotNull(baseException);
        assertEquals(message, baseException.getMessage());
    }
}
