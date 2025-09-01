package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SavingExceptionTest {

    @Test
    void testSavingExceptionWithMessage() {
        String message = "Insufficient savings balance";

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
    void testSavingExceptionWithEmptyMessage() {
        String message = "";

        SavingException savingException = new SavingException(message);

        assertNotNull(savingException);
        assertEquals("", savingException.getMessage());
    }

    @Test
    void testSavingExceptionWithLongMessage() {
        String message = "This is a very long saving error message that contains detailed information about what went wrong during the saving account processing. It should be properly handled and displayed to the user.";

        SavingException savingException = new SavingException(message);

        assertNotNull(savingException);
        assertEquals(message, savingException.getMessage());
    }
}
