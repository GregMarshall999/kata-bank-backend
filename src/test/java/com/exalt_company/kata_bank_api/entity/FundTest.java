package com.exalt_company.kata_bank_api.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
} 