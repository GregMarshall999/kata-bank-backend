package com.exalt_company.kata_bank_api.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FundOverdrawTest {
    private Fund fund;

    @BeforeEach
    void setUp() {
        fund = new Fund();
        BankUser owner = new BankUser();
        owner.setId(1L);
        fund.setOwner(owner);
    }

    @Test
    void testOverdrawCapabilitiesDefaultState() {
        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw());
    }

    @Test
    void testEnableOverdrawCapabilities() {
        double maxOverdraw = 500.0;

        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(maxOverdraw);

        assertTrue(fund.canOverdraw());
        assertEquals(maxOverdraw, fund.getMaxOverdraw());
    }

    @Test
    void testDisableOverdrawCapabilities() {
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        fund.setCanOverdraw(false);
        fund.setMaxOverdraw(0.0);

        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw());
    }

    @Test
    void testOverdrawCapabilitiesWithZeroMaxOverdraw() {
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(0.0);

        assertTrue(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw());
    }

    @Test
    void testOverdrawCapabilitiesWithNegativeMaxOverdraw() {
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(-100.0);

        assertTrue(fund.canOverdraw());
        assertEquals(-100.0, fund.getMaxOverdraw());
    }

    @Test
    void testOverdrawCapabilitiesWithLargeMaxOverdraw() {
        double largeAmount = 1000000.0;
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(largeAmount);

        assertTrue(fund.canOverdraw());
        assertEquals(largeAmount, fund.getMaxOverdraw());
    }

    @Test
    void testOverdrawCapabilitiesWithDecimalMaxOverdraw() {
        double decimalAmount = 123.45;
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(decimalAmount);

        assertTrue(fund.canOverdraw());
        assertEquals(decimalAmount, fund.getMaxOverdraw());
    }

    @Test
    void testBalanceOperationsWithOverdrawEnabled() {
        fund.setBalance(1000.0);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        fund.setBalance(fund.getBalance() - 300.0);

        assertEquals(700.0, fund.getBalance());
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw());
    }

    @Test
    void testBalanceOperationsWithOverdrawEnabledExceedingBalance() {
        fund.setBalance(100.0);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        fund.setBalance(fund.getBalance() - 200.0);

        assertEquals(-100.0, fund.getBalance());
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw());
    }

    @Test
    void testBalanceOperationsWithOverdrawEnabledAtLimit() {
        fund.setBalance(100.0);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        fund.setBalance(fund.getBalance() - 600.0);

        assertEquals(-500.0, fund.getBalance());
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw());
    }

    @Test
    void testBalanceOperationsWithOverdrawDisabled() {
        fund.setBalance(1000.0);
        fund.setCanOverdraw(false);
        fund.setMaxOverdraw(0.0);

        fund.setBalance(fund.getBalance() - 300.0);

        assertEquals(700.0, fund.getBalance());
        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw());
    }

    @Test
    void testBalanceOperationsWithOverdrawDisabledExceedingBalance() {
        fund.setBalance(100.0);
        fund.setCanOverdraw(false);
        fund.setMaxOverdraw(0.0);

        fund.setBalance(fund.getBalance() - 200.0);

        assertEquals(-100.0, fund.getBalance());
        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw());
    }

    @Test
    void testOverdrawCapabilitiesWithNullOwner() {
        fund.setOwner(null);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw());
        assertNull(fund.getOwner());
    }

    @Test
    void testOverdrawCapabilitiesWithZeroBalance() {
        fund.setBalance(0.0);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        assertEquals(0.0, fund.getBalance());
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw());
    }

    @Test
    void testOverdrawCapabilitiesWithNegativeBalance() {
        fund.setBalance(-100.0);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        assertEquals(-100.0, fund.getBalance());
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw());
    }

    @Test
    void testOverdrawCapabilitiesWithLargeBalance() {
        fund.setBalance(Double.MAX_VALUE);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        assertEquals(Double.MAX_VALUE, fund.getBalance());
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw());
    }

    @Test
    void testOverdrawCapabilitiesWithPrecisionValues() {
        fund.setBalance(100.123456789);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.987654321);

        assertEquals(100.123456789, fund.getBalance());
        assertTrue(fund.canOverdraw());
        assertEquals(500.987654321, fund.getMaxOverdraw());
    }

    @Test
    void testOverdrawCapabilitiesStateTransitions() {
        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw());

        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(300.0);

        assertTrue(fund.canOverdraw());
        assertEquals(300.0, fund.getMaxOverdraw());

        fund.setMaxOverdraw(600.0);

        assertTrue(fund.canOverdraw());
        assertEquals(600.0, fund.getMaxOverdraw());

        fund.setCanOverdraw(false);
        fund.setMaxOverdraw(0.0);

        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw());
    }

    @Test
    void testOverdrawCapabilitiesWithOwnerRelationship() {
        BankUser newOwner = new BankUser();
        newOwner.setId(2L);

        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);
        fund.setOwner(newOwner);

        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw());
        assertNotNull(fund.getOwner());
        assertEquals(2L, fund.getOwner().getId());
    }

    @Test
    void testOverdrawCapabilitiesWithBalanceChanges() {
        fund.setBalance(1000.0);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        fund.setBalance(fund.getBalance() - 200.0);
        fund.setBalance(fund.getBalance() + 100.0);
        fund.setBalance(fund.getBalance() - 400.0);
        fund.setBalance(fund.getBalance() - 200.0);

        assertEquals(300.0, fund.getBalance());
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw());
    }

    @Test
    void testOverdrawCapabilitiesWithExtremeValues() {
        fund.setBalance(Double.MIN_VALUE);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(Double.MAX_VALUE);

        assertEquals(Double.MIN_VALUE, fund.getBalance());
        assertTrue(fund.canOverdraw());
        assertEquals(Double.MAX_VALUE, fund.getMaxOverdraw());
    }
}
