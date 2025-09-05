package com.exalt_company.kata_bank_api.dto.fund;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FundDtoTest {
    private FundDto fundDto;

    @BeforeEach
    void setUp() {
        fundDto = new FundDto();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(fundDto);
        assertEquals(0L, fundDto.getId());
        assertEquals(0L, fundDto.getOwnerId());
        assertEquals(0.0, fundDto.getBalance(), 0.001);
        assertFalse(fundDto.isCanOverdraw());
        assertEquals(0.0, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testSetAndGetBalance() {
        double balance = 1500.75;
        
        fundDto.setBalance(balance);
        
        assertEquals(balance, fundDto.getBalance(), 0.001);
    }

    @Test
    void testSetAndGetCanOverdraw() {
        fundDto.setCanOverdraw(true);
        assertTrue(fundDto.isCanOverdraw());
        
        fundDto.setCanOverdraw(false);
        assertFalse(fundDto.isCanOverdraw());
    }

    @Test
    void testSetAndGetMaxOverdraw() {
        double maxOverdraw = 500.0;
        
        fundDto.setMaxOverdraw(maxOverdraw);
        
        assertEquals(maxOverdraw, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testInheritedFields() {
        long id = 1L;
        long ownerId = 123L;
        
        fundDto.setId(id);
        fundDto.setOwnerId(ownerId);
        
        assertEquals(id, fundDto.getId());
        assertEquals(ownerId, fundDto.getOwnerId());
    }

    @Test
    void testAllFieldsTogether() {
        long id = 1L;
        long ownerId = 456L;
        double balance = 2500.50;
        boolean canOverdraw = true;
        double maxOverdraw = 1000.0;
        
        fundDto.setId(id);
        fundDto.setOwnerId(ownerId);
        fundDto.setBalance(balance);
        fundDto.setCanOverdraw(canOverdraw);
        fundDto.setMaxOverdraw(maxOverdraw);
        
        assertEquals(id, fundDto.getId());
        assertEquals(ownerId, fundDto.getOwnerId());
        assertEquals(balance, fundDto.getBalance(), 0.001);
        assertEquals(canOverdraw, fundDto.isCanOverdraw());
        assertEquals(maxOverdraw, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testZeroValues() {
        fundDto.setBalance(0.0);
        fundDto.setCanOverdraw(false);
        fundDto.setMaxOverdraw(0.0);
        
        assertEquals(0.0, fundDto.getBalance(), 0.001);
        assertFalse(fundDto.isCanOverdraw());
        assertEquals(0.0, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testNegativeValues() {
        double negativeBalance = -100.0;
        double negativeMaxOverdraw = -500.0;
        
        fundDto.setBalance(negativeBalance);
        fundDto.setMaxOverdraw(negativeMaxOverdraw);
        
        assertEquals(negativeBalance, fundDto.getBalance(), 0.001);
        assertEquals(negativeMaxOverdraw, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testLargeValues() {
        double largeBalance = 999999999.99;
        double largeMaxOverdraw = Double.MAX_VALUE;
        
        fundDto.setBalance(largeBalance);
        fundDto.setMaxOverdraw(largeMaxOverdraw);
        
        assertEquals(largeBalance, fundDto.getBalance(), 0.001);
        assertEquals(largeMaxOverdraw, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testDecimalPrecision() {
        double preciseBalance = 1234.56789;
        double preciseMaxOverdraw = 9876.54321;
        
        fundDto.setBalance(preciseBalance);
        fundDto.setMaxOverdraw(preciseMaxOverdraw);
        
        assertEquals(preciseBalance, fundDto.getBalance(), 0.001);
        assertEquals(preciseMaxOverdraw, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testScientificNotation() {
        double scientificBalance = 1.23e5;
        double scientificMaxOverdraw = 5.67e6;
        
        fundDto.setBalance(scientificBalance);
        fundDto.setMaxOverdraw(scientificMaxOverdraw);
        
        assertEquals(scientificBalance, fundDto.getBalance(), 0.001);
        assertEquals(scientificMaxOverdraw, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testCanOverdrawToggle() {
        assertFalse(fundDto.isCanOverdraw());
        
        fundDto.setCanOverdraw(true);
        assertTrue(fundDto.isCanOverdraw());
        
        fundDto.setCanOverdraw(false);
        assertFalse(fundDto.isCanOverdraw());
        
        fundDto.setCanOverdraw(true);
        assertTrue(fundDto.isCanOverdraw());
    }

    @Test
    void testMultipleUpdates() {
        fundDto.setBalance(1000.0);
        fundDto.setCanOverdraw(false);
        fundDto.setMaxOverdraw(0.0);
        
        fundDto.setBalance(2000.0);
        fundDto.setCanOverdraw(true);
        fundDto.setMaxOverdraw(500.0);
        
        assertEquals(2000.0, fundDto.getBalance(), 0.001);
        assertTrue(fundDto.isCanOverdraw());
        assertEquals(500.0, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testOverdrawScenario() {
        fundDto.setBalance(-200.0);
        fundDto.setCanOverdraw(true);
        fundDto.setMaxOverdraw(500.0);
        
        assertEquals(-200.0, fundDto.getBalance(), 0.001);
        assertTrue(fundDto.isCanOverdraw());
        assertEquals(500.0, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testNoOverdrawScenario() {
        fundDto.setBalance(1000.0);
        fundDto.setCanOverdraw(false);
        fundDto.setMaxOverdraw(0.0);
        
        assertEquals(1000.0, fundDto.getBalance(), 0.001);
        assertFalse(fundDto.isCanOverdraw());
        assertEquals(0.0, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testSmallDecimalValues() {
        double smallBalance = 0.01;
        double smallMaxOverdraw = 0.02;
        
        fundDto.setBalance(smallBalance);
        fundDto.setMaxOverdraw(smallMaxOverdraw);
        
        assertEquals(smallBalance, fundDto.getBalance(), 0.001);
        assertEquals(smallMaxOverdraw, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testCurrencyLikeValues() {
        double currencyBalance = 1234.56;
        double currencyMaxOverdraw = 9876.54;
        
        fundDto.setBalance(currencyBalance);
        fundDto.setMaxOverdraw(currencyMaxOverdraw);
        
        assertEquals(currencyBalance, fundDto.getBalance(), 0.001);
        assertEquals(currencyMaxOverdraw, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testRealisticBankingScenario() {
        long id = 1001L;
        long ownerId = 5001L;
        double balance = 15000.75;
        boolean canOverdraw = true;
        double maxOverdraw = 2000.0;
        
        fundDto.setId(id);
        fundDto.setOwnerId(ownerId);
        fundDto.setBalance(balance);
        fundDto.setCanOverdraw(canOverdraw);
        fundDto.setMaxOverdraw(maxOverdraw);
        
        assertEquals(id, fundDto.getId());
        assertEquals(ownerId, fundDto.getOwnerId());
        assertEquals(balance, fundDto.getBalance(), 0.001);
        assertEquals(canOverdraw, fundDto.isCanOverdraw());
        assertEquals(maxOverdraw, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testInfinityValues() {
        fundDto.setBalance(Double.POSITIVE_INFINITY);
        fundDto.setMaxOverdraw(Double.NEGATIVE_INFINITY);
        
        assertEquals(Double.POSITIVE_INFINITY, fundDto.getBalance());
        assertEquals(Double.NEGATIVE_INFINITY, fundDto.getMaxOverdraw());
    }

    @Test
    void testNaNValues() {
        fundDto.setBalance(Double.NaN);
        fundDto.setMaxOverdraw(Double.NaN);
        
        assertEquals(Double.NaN, fundDto.getBalance());
        assertEquals(Double.NaN, fundDto.getMaxOverdraw());
    }

    @Test
    void testBalanceEqualToMaxOverdraw() {
        double sameValue = 1000.0;
        
        fundDto.setBalance(sameValue);
        fundDto.setMaxOverdraw(sameValue);
        
        assertEquals(sameValue, fundDto.getBalance(), 0.001);
        assertEquals(sameValue, fundDto.getMaxOverdraw(), 0.001);
    }

    @Test
    void testBalanceGreaterThanMaxOverdraw() {
        double balance = 2000.0;
        double maxOverdraw = 1000.0;
        
        fundDto.setBalance(balance);
        fundDto.setMaxOverdraw(maxOverdraw);
        
        assertEquals(balance, fundDto.getBalance(), 0.001);
        assertEquals(maxOverdraw, fundDto.getMaxOverdraw(), 0.001);
    }
}
