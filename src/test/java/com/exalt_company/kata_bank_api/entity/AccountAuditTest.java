package com.exalt_company.kata_bank_api.entity;

import com.exalt_company.kata_bank_api.enums.AuditOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccountAuditTest {
    private AccountAudit accountAudit;
    private BankUser testUser;
    private Fund testFund;
    private Saving testSaving;

    @BeforeEach
    void setUp() {
        accountAudit = new AccountAudit();
        
        testUser = new BankUser();
        testUser.setId(1L);
        
        testFund = new Fund();
        testFund.setId(1L);
        testFund.setBalance(1000.0);
        
        testSaving = new Saving();
        testSaving.setId(1L);
        testSaving.setBalance(500.0);
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(accountAudit);
        assertEquals(0, accountAudit.getId());
        assertEquals(0, accountAudit.getVersion());
        assertNull(accountAudit.getOperation());
        assertEquals(0.0, accountAudit.getAmount());
        assertEquals(0.0, accountAudit.getBalanceBefore());
        assertEquals(0.0, accountAudit.getBalanceAfter());
        assertNull(accountAudit.getRequestingUser());
        assertNull(accountAudit.getUserFund());
        assertNull(accountAudit.getUserSaving());
    }

    @Test
    void testSetAndGetOperation() {
        AuditOperation operation = AuditOperation.DEPOSIT;
        
        accountAudit.setOperation(operation);
        
        assertEquals(operation, accountAudit.getOperation());
    }

    @Test
    void testSetAndGetAmount() {
        double amount = 100.50;
        
        accountAudit.setAmount(amount);
        
        assertEquals(amount, accountAudit.getAmount());
    }

    @Test
    void testSetAndGetBalanceBefore() {
        double balanceBefore = 500.75;
        
        accountAudit.setBalanceBefore(balanceBefore);
        
        assertEquals(balanceBefore, accountAudit.getBalanceBefore());
    }

    @Test
    void testSetAndGetBalanceAfter() {
        double balanceAfter = 600.25;
        
        accountAudit.setBalanceAfter(balanceAfter);
        
        assertEquals(balanceAfter, accountAudit.getBalanceAfter());
    }

    @Test
    void testSetAndGetRequestingUser() {
        accountAudit.setRequestingUser(testUser);
        
        assertEquals(testUser, accountAudit.getRequestingUser());
    }

    @Test
    void testSetAndGetUserFund() {
        accountAudit.setUserFund(testFund);
        
        assertEquals(testFund, accountAudit.getUserFund());
    }

    @Test
    void testSetAndGetUserSaving() {
        accountAudit.setUserSaving(testSaving);
        
        assertEquals(testSaving, accountAudit.getUserSaving());
    }

    @Test
    void testInheritedIdAndVersion() {
        accountAudit.setId(123L);
        accountAudit.setVersion(5);
        
        assertEquals(123L, accountAudit.getId());
        assertEquals(5, accountAudit.getVersion());
    }

    @Test
    void testAllFieldsTogether() {
        AuditOperation operation = AuditOperation.WITHDRAW;
        double amount = 75.25;
        double balanceBefore = 1000.0;
        double balanceAfter = 924.75;
        
        accountAudit.setOperation(operation);
        accountAudit.setAmount(amount);
        accountAudit.setBalanceBefore(balanceBefore);
        accountAudit.setBalanceAfter(balanceAfter);
        accountAudit.setRequestingUser(testUser);
        accountAudit.setUserFund(testFund);
        accountAudit.setUserSaving(null);
        
        assertEquals(operation, accountAudit.getOperation());
        assertEquals(amount, accountAudit.getAmount());
        assertEquals(balanceBefore, accountAudit.getBalanceBefore());
        assertEquals(balanceAfter, accountAudit.getBalanceAfter());
        assertEquals(testUser, accountAudit.getRequestingUser());
        assertEquals(testFund, accountAudit.getUserFund());
        assertNull(accountAudit.getUserSaving());
    }

    @Test
    void testNullValues() {
        accountAudit.setOperation(null);
        accountAudit.setRequestingUser(null);
        accountAudit.setUserFund(null);
        accountAudit.setUserSaving(null);
        
        assertNull(accountAudit.getOperation());
        assertNull(accountAudit.getRequestingUser());
        assertNull(accountAudit.getUserFund());
        assertNull(accountAudit.getUserSaving());
    }

    @Test
    void testDecimalPrecision() {
        double preciseAmount = 123.456789;
        double preciseBalance = 999.999999;
        
        accountAudit.setAmount(preciseAmount);
        accountAudit.setBalanceBefore(preciseBalance);
        accountAudit.setBalanceAfter(preciseBalance + preciseAmount);
        
        assertEquals(preciseAmount, accountAudit.getAmount());
        assertEquals(preciseBalance, accountAudit.getBalanceBefore());
        assertEquals(preciseBalance + preciseAmount, accountAudit.getBalanceAfter());
    }

    @Test
    void testNegativeValues() {
        double negativeAmount = -50.0;
        double negativeBalance = -100.0;
        
        accountAudit.setAmount(negativeAmount);
        accountAudit.setBalanceBefore(negativeBalance);
        accountAudit.setBalanceAfter(negativeBalance + negativeAmount);
        
        assertEquals(negativeAmount, accountAudit.getAmount());
        assertEquals(negativeBalance, accountAudit.getBalanceBefore());
        assertEquals(negativeBalance + negativeAmount, accountAudit.getBalanceAfter());
    }

    @Test
    void testZeroValues() {
        double zeroAmount = 0.0;
        double zeroBalance = 0.0;
        
        accountAudit.setAmount(zeroAmount);
        accountAudit.setBalanceBefore(zeroBalance);
        accountAudit.setBalanceAfter(zeroBalance);
        
        assertEquals(zeroAmount, accountAudit.getAmount());
        assertEquals(zeroBalance, accountAudit.getBalanceBefore());
        assertEquals(zeroBalance, accountAudit.getBalanceAfter());
    }
}
