package com.exalt_company.kata_bank_api.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SavingTest {
    private Saving saving;
    private BankUser owner;

    @BeforeEach
    void setUp() {
        saving = new Saving();
        owner = new BankUser();
        owner.setId(1L);
    }

    @Test
    void testSavingCreation() {
        double balance = 1000.0D;
        double maxBalance = 5000.0D;
        
        saving.setBalance(balance);
        saving.setMaxBalance(maxBalance);
        saving.setOwner(owner);
        
        assertEquals(balance, saving.getBalance());
        assertEquals(maxBalance, saving.getMaxBalance());
        assertNotNull(saving.getOwner());
        assertEquals(1L, saving.getOwner().getId());
    }

    @Test
    void testBalanceOperations() {
        saving.setBalance(500.0D);
        saving.setMaxBalance(2000.0D);
        
        saving.setBalance(saving.getBalance() + 300.0D);
        
        assertEquals(800.0D, saving.getBalance());
        
        saving.setBalance(saving.getBalance() - 200.0D);
        
        assertEquals(600.0D, saving.getBalance());
    }

    @Test
    void testMaxBalanceOperations() {
        saving.setMaxBalance(1000.0D);
        
        saving.setMaxBalance(saving.getMaxBalance() + 500.0D);
        
        assertEquals(1500.0D, saving.getMaxBalance());
        
        saving.setMaxBalance(saving.getMaxBalance() - 200.0D);
        
        assertEquals(1300.0D, saving.getMaxBalance());
    }

    @Test
    void testOwnerRelationship() {
        BankUser newOwner = new BankUser();
        newOwner.setId(2L);

        saving.setOwner(newOwner);
        
        assertNotNull(saving.getOwner());
        assertEquals(2L, saving.getOwner().getId());
    }

    @Test
    void testZeroBalance() {
        saving.setBalance(0.0D);
        saving.setMaxBalance(1000.0D);
        
        assertEquals(0.0D, saving.getBalance());
        assertEquals(1000.0D, saving.getMaxBalance());
    }

    @Test
    void testZeroMaxBalance() {
        saving.setBalance(100.0D);
        saving.setMaxBalance(0.0D);
        
        assertEquals(100.0D, saving.getBalance());
        assertEquals(0.0D, saving.getMaxBalance());
    }

    @Test
    void testNegativeBalance() {
        saving.setBalance(-100.0D);
        saving.setMaxBalance(1000.0D);
        
        assertEquals(-100.0D, saving.getBalance());
        assertEquals(1000.0D, saving.getMaxBalance());
    }

    @Test
    void testNegativeMaxBalance() {
        saving.setBalance(100.0D);
        saving.setMaxBalance(-500.0D);
        
        assertEquals(100.0D, saving.getBalance());
        assertEquals(-500.0D, saving.getMaxBalance());
    }

    @Test
    void testLargeBalance() {
        saving.setBalance(Double.MAX_VALUE);
        saving.setMaxBalance(Double.MAX_VALUE);
        
        assertEquals(Double.MAX_VALUE, saving.getBalance());
        assertEquals(Double.MAX_VALUE, saving.getMaxBalance());
    }

    @Test
    void testBalanceEqualsMaxBalance() {
        double amount = 1500.0D;
        saving.setBalance(amount);
        saving.setMaxBalance(amount);
        
        assertEquals(amount, saving.getBalance());
        assertEquals(amount, saving.getMaxBalance());
    }

    @Test
    void testBalanceExceedsMaxBalance() {
        saving.setBalance(2000.0D);
        saving.setMaxBalance(1000.0D);
        
        assertEquals(2000.0D, saving.getBalance());
        assertEquals(1000.0D, saving.getMaxBalance());
    }

    @Test
    void testCompleteSavingSetup() {
        double balance = 2500.0D;
        double maxBalance = 10000.0D;
        
        saving.setBalance(balance);
        saving.setMaxBalance(maxBalance);
        saving.setOwner(owner);
        
        assertEquals(balance, saving.getBalance());
        assertEquals(maxBalance, saving.getMaxBalance());
        assertNotNull(saving.getOwner());
        assertEquals(1L, saving.getOwner().getId());
    }
}
