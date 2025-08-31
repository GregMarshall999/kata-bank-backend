package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FundExceptionTest {

    @Test
    void testFundExceptionWithMessageAndBanking() {
        String message = "Insufficient funds";

        FundException fundException = new FundException(message);

        assertNotNull(fundException);
        assertEquals(message, fundException.getMessage());
    }

    @Test
    void testFundExceptionWithNullMessage() {
        String message = null;

        FundException fundException = new FundException(message);

        assertNotNull(fundException);
        assertNull(fundException.getMessage());
    }

    @Test
    void testFundExceptionWithEmptyMessage() {
        String message = "";

        FundException fundException = new FundException(message);

        assertNotNull(fundException);
        assertEquals("", fundException.getMessage());
    }

    @Test
    void testFundExceptionWithLongMessage() {
        String message = "This is a very long fund error message that contains detailed information about what went wrong during the fund processing. It should be properly handled and displayed to the user.";

        FundException fundException = new FundException(message);

        assertNotNull(fundException);
        assertEquals(message, fundException.getMessage());
    }
}
