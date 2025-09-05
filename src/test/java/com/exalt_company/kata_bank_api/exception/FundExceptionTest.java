package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FundExceptionTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "Insufficient funds",
            "",
            "This is a very long fund error message that contains detailed information about what went wrong during the fund processing. It should be properly handled and displayed to the user.",
            "Fund error with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?",
            "Fund error with unicode: éèêëàâäôöùûüçñ",
            "   Fund exception with whitespace   ",
            "Fund exception\nwith newline\ncharacters",
            "Fund exception\twith tab\tcharacters",
            "Fund exception with numbers: 1234567890",
            "Fund exception with mixed content: 123 ABC !@# éèê 中文"
    })
    void testConstructor(String message) {
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
    void testFundExceptionToString() {
        String message = "Fund exception toString test";
        FundException fundException = new FundException(message);

        assertNotNull(fundException);
        String toString = fundException.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("FundException"));
        assertTrue(toString.contains(message));
    }

    @Test
    void testFundExceptionWithVeryLongMessage() {
        String message = "a".repeat(10000);

        FundException fundException = new FundException(message);

        assertNotNull(fundException);
        assertEquals(message, fundException.getMessage());
    }

    @Test
    void testFundExceptionMultipleInstances() {
        String message1 = "First fund exception";
        String message2 = "Second fund exception";

        FundException exception1 = new FundException(message1);
        FundException exception2 = new FundException(message2);

        assertNotNull(exception1);
        assertNotNull(exception2);
        assertEquals(message1, exception1.getMessage());
        assertEquals(message2, exception2.getMessage());
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    void testFundExceptionWithCause() {
        String message = "Fund exception with cause";
        Exception cause = new RuntimeException("Root cause");
        FundException fundException = new FundException(message);
        fundException.initCause(cause);

        assertNotNull(fundException);
        assertEquals(message, fundException.getMessage());
        assertEquals(cause, fundException.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Insufficient funds for withdrawal",
            "Overdraw limit exceeded",
            "Fund account not found",
            "Invalid fund operation",
            "Fund transfer failed due to insufficient balance",
            "Maximum overdraw amount exceeded",
            "Fund account is frozen",
            "Invalid fund amount specified"
    })
    void testFundExceptionRealisticScenarios(String errorMessage) {
        FundException fundException = new FundException(errorMessage);
        assertNotNull(fundException);
        assertEquals(errorMessage, fundException.getMessage());
    }
}
