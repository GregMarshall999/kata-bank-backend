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

    @Test
    void testAllAuditOperations() {
        accountAudit.setOperation(AuditOperation.DEPOSIT);
        assertEquals(AuditOperation.DEPOSIT, accountAudit.getOperation());
        
        accountAudit.setOperation(AuditOperation.WITHDRAW);
        assertEquals(AuditOperation.WITHDRAW, accountAudit.getOperation());
        
        accountAudit.setOperation(AuditOperation.OPEN);
        assertEquals(AuditOperation.OPEN, accountAudit.getOperation());
        
        accountAudit.setOperation(AuditOperation.CLOSE);
        assertEquals(AuditOperation.CLOSE, accountAudit.getOperation());
        
        accountAudit.setOperation(AuditOperation.OVERDRAW_REQUEST);
        assertEquals(AuditOperation.OVERDRAW_REQUEST, accountAudit.getOperation());
        
        accountAudit.setOperation(AuditOperation.OVERDRAW_CANCEL);
        assertEquals(AuditOperation.OVERDRAW_CANCEL, accountAudit.getOperation());
    }

    @Test
    void testLargeValues() {
        double largeAmount = 1000000.0;
        double largeBalance = 999999999.99;
        
        accountAudit.setAmount(largeAmount);
        accountAudit.setBalanceBefore(largeBalance);
        accountAudit.setBalanceAfter(largeBalance + largeAmount);
        
        assertEquals(largeAmount, accountAudit.getAmount());
        assertEquals(largeBalance, accountAudit.getBalanceBefore());
        assertEquals(largeBalance + largeAmount, accountAudit.getBalanceAfter());
    }

    @Test
    void testScientificNotation() {
        double scientificAmount = 1.23E6;
        double scientificBalance = 5.67E-3;
        
        accountAudit.setAmount(scientificAmount);
        accountAudit.setBalanceBefore(scientificBalance);
        accountAudit.setBalanceAfter(scientificBalance + scientificAmount);
        
        assertEquals(scientificAmount, accountAudit.getAmount());
        assertEquals(scientificBalance, accountAudit.getBalanceBefore());
        assertEquals(scientificBalance + scientificAmount, accountAudit.getBalanceAfter());
    }

    @Test
    void testUserAndFundIndependence() {
        BankUser user1 = new BankUser();
        user1.setId(100L);
        Fund fund1 = new Fund();
        fund1.setId(200L);
        
        accountAudit.setRequestingUser(user1);
        accountAudit.setUserFund(fund1);
        
        assertEquals(user1, accountAudit.getRequestingUser());
        assertEquals(fund1, accountAudit.getUserFund());
        
        BankUser user2 = new BankUser();
        user2.setId(300L);
        accountAudit.setRequestingUser(user2);
        
        assertEquals(user2, accountAudit.getRequestingUser());
        assertEquals(fund1, accountAudit.getUserFund());
    }

    @Test
    void testUserAndSavingIndependence() {
        BankUser user1 = new BankUser();
        user1.setId(100L);
        Saving saving1 = new Saving();
        saving1.setId(200L);
        
        accountAudit.setRequestingUser(user1);
        accountAudit.setUserSaving(saving1);
        
        assertEquals(user1, accountAudit.getRequestingUser());
        assertEquals(saving1, accountAudit.getUserSaving());
        
        Saving saving2 = new Saving();
        saving2.setId(300L);
        accountAudit.setUserSaving(saving2);
        
        assertEquals(user1, accountAudit.getRequestingUser());
        assertEquals(saving2, accountAudit.getUserSaving());
    }

    @Test
    void testFundAndSavingMutualExclusivity() {
        Fund fund = new Fund();
        fund.setId(100L);
        Saving saving = new Saving();
        saving.setId(200L);
        
        accountAudit.setUserFund(fund);
        accountAudit.setUserSaving(saving);
        
        assertEquals(fund, accountAudit.getUserFund());
        assertEquals(saving, accountAudit.getUserSaving());
        
        accountAudit.setUserFund(null);
        
        assertNull(accountAudit.getUserFund());
        assertEquals(saving, accountAudit.getUserSaving());
        
        accountAudit.setUserSaving(null);
        accountAudit.setUserFund(fund);
        
        assertEquals(fund, accountAudit.getUserFund());
        assertNull(accountAudit.getUserSaving());
    }

    @Test
    void testOperationAndAmountIndependence() {
        accountAudit.setOperation(AuditOperation.DEPOSIT);
        accountAudit.setAmount(100.0);
        
        assertEquals(AuditOperation.DEPOSIT, accountAudit.getOperation());
        assertEquals(100.0, accountAudit.getAmount());
        
        accountAudit.setOperation(AuditOperation.WITHDRAW);
        
        assertEquals(AuditOperation.WITHDRAW, accountAudit.getOperation());
        assertEquals(100.0, accountAudit.getAmount());
    }

    @Test
    void testBalanceBeforeAndAfterIndependence() {
        accountAudit.setBalanceBefore(1000.0);
        accountAudit.setBalanceAfter(1100.0);
        
        assertEquals(1000.0, accountAudit.getBalanceBefore());
        assertEquals(1100.0, accountAudit.getBalanceAfter());
        
        accountAudit.setBalanceBefore(900.0);
        
        assertEquals(900.0, accountAudit.getBalanceBefore());
        assertEquals(1100.0, accountAudit.getBalanceAfter());
    }

    @Test
    void testRealisticDepositScenario() {
        long id = 1001L;
        int version = 3;
        BankUser user = new BankUser();
        user.setId(5001L);
        Fund fund = new Fund();
        fund.setId(6001L);
        AuditOperation operation = AuditOperation.DEPOSIT;
        double amount = 2500.50;
        double balanceBefore = 10000.0;
        double balanceAfter = 12500.50;
        
        accountAudit.setId(id);
        accountAudit.setVersion(version);
        accountAudit.setRequestingUser(user);
        accountAudit.setUserFund(fund);
        accountAudit.setOperation(operation);
        accountAudit.setAmount(amount);
        accountAudit.setBalanceBefore(balanceBefore);
        accountAudit.setBalanceAfter(balanceAfter);
        
        assertEquals(id, accountAudit.getId());
        assertEquals(version, accountAudit.getVersion());
        assertEquals(user, accountAudit.getRequestingUser());
        assertEquals(fund, accountAudit.getUserFund());
        assertEquals(operation, accountAudit.getOperation());
        assertEquals(amount, accountAudit.getAmount());
        assertEquals(balanceBefore, accountAudit.getBalanceBefore());
        assertEquals(balanceAfter, accountAudit.getBalanceAfter());
    }

    @Test
    void testRealisticWithdrawalScenario() {
        long id = 1002L;
        int version = 4;
        BankUser user = new BankUser();
        user.setId(5002L);
        Saving saving = new Saving();
        saving.setId(6002L);
        AuditOperation operation = AuditOperation.WITHDRAW;
        double amount = 750.25;
        double balanceBefore = 5000.0;
        double balanceAfter = 4249.75;
        
        accountAudit.setId(id);
        accountAudit.setVersion(version);
        accountAudit.setRequestingUser(user);
        accountAudit.setUserSaving(saving);
        accountAudit.setOperation(operation);
        accountAudit.setAmount(amount);
        accountAudit.setBalanceBefore(balanceBefore);
        accountAudit.setBalanceAfter(balanceAfter);
        
        assertEquals(id, accountAudit.getId());
        assertEquals(version, accountAudit.getVersion());
        assertEquals(user, accountAudit.getRequestingUser());
        assertEquals(saving, accountAudit.getUserSaving());
        assertEquals(operation, accountAudit.getOperation());
        assertEquals(amount, accountAudit.getAmount());
        assertEquals(balanceBefore, accountAudit.getBalanceBefore());
        assertEquals(balanceAfter, accountAudit.getBalanceAfter());
    }

    @Test
    void testRealisticTransferScenario() {
        long id = 1003L;
        int version = 5;
        BankUser user = new BankUser();
        user.setId(5003L);
        Fund fund = new Fund();
        fund.setId(6003L);
        AuditOperation operation = AuditOperation.OPEN;
        double amount = 10000.0;
        double balanceBefore = 50000.0;
        double balanceAfter = 40000.0;
        
        accountAudit.setId(id);
        accountAudit.setVersion(version);
        accountAudit.setRequestingUser(user);
        accountAudit.setUserFund(fund);
        accountAudit.setOperation(operation);
        accountAudit.setAmount(amount);
        accountAudit.setBalanceBefore(balanceBefore);
        accountAudit.setBalanceAfter(balanceAfter);
        
        assertEquals(id, accountAudit.getId());
        assertEquals(version, accountAudit.getVersion());
        assertEquals(user, accountAudit.getRequestingUser());
        assertEquals(fund, accountAudit.getUserFund());
        assertEquals(operation, accountAudit.getOperation());
        assertEquals(amount, accountAudit.getAmount());
        assertEquals(balanceBefore, accountAudit.getBalanceBefore());
        assertEquals(balanceAfter, accountAudit.getBalanceAfter());
    }

    @Test
    void testRealisticOverdrawScenario() {
        long id = 1004L;
        int version = 6;
        BankUser user = new BankUser();
        user.setId(5004L);
        Fund fund = new Fund();
        fund.setId(6004L);
        AuditOperation operation = AuditOperation.OVERDRAW_REQUEST;
        double amount = -500.0;
        double balanceBefore = 100.0;
        double balanceAfter = -400.0;
        
        accountAudit.setId(id);
        accountAudit.setVersion(version);
        accountAudit.setRequestingUser(user);
        accountAudit.setUserFund(fund);
        accountAudit.setOperation(operation);
        accountAudit.setAmount(amount);
        accountAudit.setBalanceBefore(balanceBefore);
        accountAudit.setBalanceAfter(balanceAfter);
        
        assertEquals(id, accountAudit.getId());
        assertEquals(version, accountAudit.getVersion());
        assertEquals(user, accountAudit.getRequestingUser());
        assertEquals(fund, accountAudit.getUserFund());
        assertEquals(operation, accountAudit.getOperation());
        assertEquals(amount, accountAudit.getAmount());
        assertEquals(balanceBefore, accountAudit.getBalanceBefore());
        assertEquals(balanceAfter, accountAudit.getBalanceAfter());
    }

    @Test
    void testEdgeCaseAmounts() {
        double tinyAmount = 0.01;
        accountAudit.setAmount(tinyAmount);
        assertEquals(tinyAmount, accountAudit.getAmount());
        
        double hugeAmount = 999999999.99;
        accountAudit.setAmount(hugeAmount);
        assertEquals(hugeAmount, accountAudit.getAmount());
        
        double negativeAmount = -0.01;
        accountAudit.setAmount(negativeAmount);
        assertEquals(negativeAmount, accountAudit.getAmount());
    }

    @Test
    void testEdgeCaseBalances() {
        double tinyBalance = 0.01;
        accountAudit.setBalanceBefore(tinyBalance);
        accountAudit.setBalanceAfter(tinyBalance);
        assertEquals(tinyBalance, accountAudit.getBalanceBefore());
        assertEquals(tinyBalance, accountAudit.getBalanceAfter());
        
        double hugeBalance = 999999999.99;
        accountAudit.setBalanceBefore(hugeBalance);
        accountAudit.setBalanceAfter(hugeBalance);
        assertEquals(hugeBalance, accountAudit.getBalanceBefore());
        assertEquals(hugeBalance, accountAudit.getBalanceAfter());
        
        double negativeBalance = -100.0;
        accountAudit.setBalanceBefore(negativeBalance);
        accountAudit.setBalanceAfter(negativeBalance);
        assertEquals(negativeBalance, accountAudit.getBalanceBefore());
        assertEquals(negativeBalance, accountAudit.getBalanceAfter());
    }
}
