package com.exalt_company.kata_bank_api.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FundTest {
    private Fund fund;
    private BankUser owner;

    @BeforeEach
    void setUp() {
        fund = new Fund();
        owner = new BankUser();
        owner.setId(1L);
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(fund);
        assertEquals(0L, fund.getId());
        assertEquals(0, fund.getVersion());
        assertEquals(0.0, fund.getBalance(), 0.001);
        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw(), 0.001);
        assertNull(fund.getOwner());
    }

    @Test
    void testFundCreation() {
        double balance = 1000.0D;
        
        fund.setBalance(balance);
        fund.setOwner(owner);
        
        assertEquals(balance, fund.getBalance());
        assertNotNull(fund.getOwner());
        assertEquals(1L, fund.getOwner().getId());
    }

    @Test
    void testBalanceOperations() {
        fund.setBalance(500.0D);
        
        fund.setBalance(fund.getBalance() + 300.0D);
        
        assertEquals(800.0D, fund.getBalance());
        
        fund.setBalance(fund.getBalance() - 200.0D);
        
        assertEquals(600.0D, fund.getBalance());
    }

    @Test
    void testOwnerRelationship() {
        BankUser newOwner = new BankUser();
        newOwner.setId(2L);

        fund.setOwner(newOwner);
        
        assertNotNull(fund.getOwner());
        assertEquals(2L, fund.getOwner().getId());
    }

    @Test
    void testZeroBalance() {
        fund.setBalance(0.0D);
        
        assertEquals(0.0D, fund.getBalance());
    }

    @Test
    void testNegativeBalance() {
        fund.setBalance(-100.0D);
        
        assertEquals(-100.0D, fund.getBalance());
    }

    @Test
    void testLargeBalance() {
        fund.setBalance(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, fund.getBalance());
    }

    @Test
    void testOverdrawCapabilitiesDefaultState() {
        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testEnableOverdrawCapabilities() {
        double maxOverdraw = 500.0;

        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(maxOverdraw);

        assertTrue(fund.canOverdraw());
        assertEquals(maxOverdraw, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testDisableOverdrawCapabilities() {
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        fund.setCanOverdraw(false);
        fund.setMaxOverdraw(0.0);

        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testOverdrawCapabilitiesWithZeroMaxOverdraw() {
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(0.0);

        assertTrue(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testOverdrawCapabilitiesWithNegativeMaxOverdraw() {
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(-100.0);

        assertTrue(fund.canOverdraw());
        assertEquals(-100.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testOverdrawCapabilitiesWithLargeMaxOverdraw() {
        double largeAmount = 1000000.0;
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(largeAmount);

        assertTrue(fund.canOverdraw());
        assertEquals(largeAmount, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testOverdrawCapabilitiesWithDecimalMaxOverdraw() {
        double decimalAmount = 123.45;
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(decimalAmount);

        assertTrue(fund.canOverdraw());
        assertEquals(decimalAmount, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testBalanceOperationsWithOverdrawEnabled() {
        fund.setBalance(1000.0);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        fund.setBalance(fund.getBalance() - 300.0);

        assertEquals(700.0, fund.getBalance(), 0.001);
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testBalanceOperationsWithOverdrawEnabledExceedingBalance() {
        fund.setBalance(100.0);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        fund.setBalance(fund.getBalance() - 200.0);

        assertEquals(-100.0, fund.getBalance(), 0.001);
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testBalanceOperationsWithOverdrawEnabledAtLimit() {
        fund.setBalance(100.0);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        fund.setBalance(fund.getBalance() - 600.0);

        assertEquals(-500.0, fund.getBalance(), 0.001);
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testBalanceOperationsWithOverdrawDisabled() {
        fund.setBalance(1000.0);
        fund.setCanOverdraw(false);
        fund.setMaxOverdraw(0.0);

        fund.setBalance(fund.getBalance() - 300.0);

        assertEquals(700.0, fund.getBalance(), 0.001);
        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testBalanceOperationsWithOverdrawDisabledExceedingBalance() {
        fund.setBalance(100.0);
        fund.setCanOverdraw(false);
        fund.setMaxOverdraw(0.0);

        fund.setBalance(fund.getBalance() - 200.0);

        assertEquals(-100.0, fund.getBalance(), 0.001);
        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testOverdrawCapabilitiesWithNullOwner() {
        fund.setOwner(null);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw(), 0.001);
        assertNull(fund.getOwner());
    }

    @Test
    void testOverdrawCapabilitiesWithZeroBalance() {
        fund.setBalance(0.0);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        assertEquals(0.0, fund.getBalance(), 0.001);
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testOverdrawCapabilitiesWithNegativeBalance() {
        fund.setBalance(-100.0);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        assertEquals(-100.0, fund.getBalance(), 0.001);
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testOverdrawCapabilitiesWithLargeBalance() {
        fund.setBalance(Double.MAX_VALUE);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);

        assertEquals(Double.MAX_VALUE, fund.getBalance());
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testOverdrawCapabilitiesWithPrecisionValues() {
        fund.setBalance(100.123456789);
        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.987654321);

        assertEquals(100.123456789, fund.getBalance(), 0.001);
        assertTrue(fund.canOverdraw());
        assertEquals(500.987654321, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testOverdrawCapabilitiesStateTransitions() {
        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw(), 0.001);

        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(300.0);

        assertTrue(fund.canOverdraw());
        assertEquals(300.0, fund.getMaxOverdraw(), 0.001);

        fund.setMaxOverdraw(600.0);

        assertTrue(fund.canOverdraw());
        assertEquals(600.0, fund.getMaxOverdraw(), 0.001);

        fund.setCanOverdraw(false);
        fund.setMaxOverdraw(0.0);

        assertFalse(fund.canOverdraw());
        assertEquals(0.0, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testOverdrawCapabilitiesWithOwnerRelationship() {
        BankUser newOwner = new BankUser();
        newOwner.setId(2L);

        fund.setCanOverdraw(true);
        fund.setMaxOverdraw(500.0);
        fund.setOwner(newOwner);

        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw(), 0.001);
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

        assertEquals(300.0, fund.getBalance(), 0.001);
        assertTrue(fund.canOverdraw());
        assertEquals(500.0, fund.getMaxOverdraw(), 0.001);
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

    @Test
    void testAllFieldsTogether() {
        long id = 1L;
        int version = 2;
        double balance = 1500.75;
        boolean canOverdraw = true;
        double maxOverdraw = 1000.0;
        BankUser bankUser = new BankUser();
        bankUser.setId(100L);
        
        fund.setId(id);
        fund.setVersion(version);
        fund.setBalance(balance);
        fund.setCanOverdraw(canOverdraw);
        fund.setMaxOverdraw(maxOverdraw);
        fund.setOwner(bankUser);
        
        assertEquals(id, fund.getId());
        assertEquals(version, fund.getVersion());
        assertEquals(balance, fund.getBalance(), 0.001);
        assertEquals(canOverdraw, fund.canOverdraw());
        assertEquals(maxOverdraw, fund.getMaxOverdraw(), 0.001);
        assertEquals(bankUser, fund.getOwner());
    }

    @Test
    void testDecimalPrecision() {
        double preciseBalance = 1234.56789;
        double preciseMaxOverdraw = 9876.54321;
        
        fund.setBalance(preciseBalance);
        fund.setMaxOverdraw(preciseMaxOverdraw);
        
        assertEquals(preciseBalance, fund.getBalance(), 0.001);
        assertEquals(preciseMaxOverdraw, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testScientificNotation() {
        double scientificBalance = 1.23e5;
        double scientificMaxOverdraw = 5.67e6;
        
        fund.setBalance(scientificBalance);
        fund.setMaxOverdraw(scientificMaxOverdraw);
        
        assertEquals(scientificBalance, fund.getBalance(), 0.001);
        assertEquals(scientificMaxOverdraw, fund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testInfinityValues() {
        fund.setBalance(Double.POSITIVE_INFINITY);
        fund.setMaxOverdraw(Double.NEGATIVE_INFINITY);
        
        assertEquals(Double.POSITIVE_INFINITY, fund.getBalance());
        assertEquals(Double.NEGATIVE_INFINITY, fund.getMaxOverdraw());
    }

    @Test
    void testNaNValues() {
        fund.setBalance(Double.NaN);
        fund.setMaxOverdraw(Double.NaN);
        
        assertEquals(Double.NaN, fund.getBalance());
        assertEquals(Double.NaN, fund.getMaxOverdraw());
    }
} 