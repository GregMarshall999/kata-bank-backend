package com.exalt_company.kata_bank_api.dto.fund;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FundOpDtoTest {
    private FundOpDto fundOpDto;
    private Validator validator;

    @BeforeEach
    void setUp() {
        fundOpDto = new FundOpDto();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(fundOpDto);
        assertEquals(0L, fundOpDto.getId());
        assertEquals(0L, fundOpDto.getOwnerId());
        assertEquals(0.0, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testSetAndGetBalance() {
        double balance = 100.0;
        
        fundOpDto.setBalance(balance);
        
        assertEquals(balance, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testInheritedFields() {
        long id = 1L;
        long ownerId = 123L;
        
        fundOpDto.setId(id);
        fundOpDto.setOwnerId(ownerId);
        
        assertEquals(id, fundOpDto.getId());
        assertEquals(ownerId, fundOpDto.getOwnerId());
    }

    @Test
    void testAllFieldsTogether() {
        long id = 1L;
        long ownerId = 456L;
        double balance = 250.75;
        
        fundOpDto.setId(id);
        fundOpDto.setOwnerId(ownerId);
        fundOpDto.setBalance(balance);
        
        assertEquals(id, fundOpDto.getId());
        assertEquals(ownerId, fundOpDto.getOwnerId());
        assertEquals(balance, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testZeroBalance() {
        fundOpDto.setBalance(0.0);
        
        assertEquals(0.0, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testNegativeBalance() {
        double negativeBalance = -50.0;
        
        fundOpDto.setBalance(negativeBalance);
        
        assertEquals(negativeBalance, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testLargeBalance() {
        double largeBalance = 999999999.99;
        
        fundOpDto.setBalance(largeBalance);
        
        assertEquals(largeBalance, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testDecimalPrecision() {
        double preciseBalance = 123.456789;
        
        fundOpDto.setBalance(preciseBalance);
        
        assertEquals(preciseBalance, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testScientificNotation() {
        double scientificBalance = 1.23e3;
        
        fundOpDto.setBalance(scientificBalance);
        
        assertEquals(scientificBalance, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testMultipleBalanceUpdates() {
        fundOpDto.setBalance(100.0);
        assertEquals(100.0, fundOpDto.getBalance(), 0.001);
        
        fundOpDto.setBalance(200.0);
        assertEquals(200.0, fundOpDto.getBalance(), 0.001);
        
        fundOpDto.setBalance(300.0);
        assertEquals(300.0, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testDepositScenario() {
        long id = 1L;
        long ownerId = 100L;
        double depositAmount = 500.0;
        
        fundOpDto.setId(id);
        fundOpDto.setOwnerId(ownerId);
        fundOpDto.setBalance(depositAmount);
        
        assertEquals(id, fundOpDto.getId());
        assertEquals(ownerId, fundOpDto.getOwnerId());
        assertEquals(depositAmount, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testWithdrawScenario() {
        long id = 2L;
        long ownerId = 200L;
        double withdrawAmount = -250.0;
        
        fundOpDto.setId(id);
        fundOpDto.setOwnerId(ownerId);
        fundOpDto.setBalance(withdrawAmount);
        
        assertEquals(id, fundOpDto.getId());
        assertEquals(ownerId, fundOpDto.getOwnerId());
        assertEquals(withdrawAmount, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testSmallDecimalValues() {
        double smallBalance = 0.01;
        
        fundOpDto.setBalance(smallBalance);
        
        assertEquals(smallBalance, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testCurrencyLikeValues() {
        double currencyBalance = 1234.56;
        
        fundOpDto.setBalance(currencyBalance);
        
        assertEquals(currencyBalance, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testRealisticBankingScenario() {
        long id = 1001L;
        long ownerId = 5001L;
        double operationAmount = 1500.75;
        
        fundOpDto.setId(id);
        fundOpDto.setOwnerId(ownerId);
        fundOpDto.setBalance(operationAmount);
        
        assertEquals(id, fundOpDto.getId());
        assertEquals(ownerId, fundOpDto.getOwnerId());
        assertEquals(operationAmount, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testInfinityValues() {
        fundOpDto.setBalance(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, fundOpDto.getBalance());
        
        fundOpDto.setBalance(Double.NEGATIVE_INFINITY);
        assertEquals(Double.NEGATIVE_INFINITY, fundOpDto.getBalance());
    }

    @Test
    void testNaNValue() {
        fundOpDto.setBalance(Double.NaN);
        assertEquals(Double.NaN, fundOpDto.getBalance());
    }

    @Test
    void testOwnerIdWithDifferentValues() {
        long[] ownerIds = {1L, 10L, 100L, 1000L, 10000L, 100000L};
        
        for (long ownerId : ownerIds) {
            fundOpDto.setOwnerId(ownerId);
            assertEquals(ownerId, fundOpDto.getOwnerId());
        }
    }

    @Test
    void testBalanceIndependenceFromOtherFields() {
        long id = 1L;
        long ownerId = 100L;
        double balance = 500.0;
        
        fundOpDto.setId(id);
        fundOpDto.setOwnerId(ownerId);
        fundOpDto.setBalance(balance);
        
        assertEquals(id, fundOpDto.getId());
        assertEquals(ownerId, fundOpDto.getOwnerId());
        assertEquals(balance, fundOpDto.getBalance(), 0.001);
        
        fundOpDto.setBalance(750.0);
        
        assertEquals(id, fundOpDto.getId());
        assertEquals(ownerId, fundOpDto.getOwnerId());
        assertEquals(750.0, fundOpDto.getBalance(), 0.001);
    }

    @Test
    void testMultipleOperations() {
        double[] operationAmounts = {100.0, -50.0, 200.0, -75.0, 300.0};
        
        for (double amount : operationAmounts) {
            fundOpDto.setBalance(amount);
            assertEquals(amount, fundOpDto.getBalance(), 0.001);
        }
    }

    @Test
    void testZeroOwnerId() {
        fundOpDto.setOwnerId(0L);
        
        assertEquals(0L, fundOpDto.getOwnerId());
    }

    @Test
    void testNegativeOwnerId() {
        fundOpDto.setOwnerId(-1L);
        
        assertEquals(-1L, fundOpDto.getOwnerId());
    }

    @Test
    void testMaxOwnerId() {
        fundOpDto.setOwnerId(Long.MAX_VALUE);
        
        assertEquals(Long.MAX_VALUE, fundOpDto.getOwnerId());
    }

    @Test
    void testMinOwnerId() {
        fundOpDto.setOwnerId(Long.MIN_VALUE);
        
        assertEquals(Long.MIN_VALUE, fundOpDto.getOwnerId());
    }

    @Test
    void testValidFundOpDto() {
        fundOpDto.setOwnerId(1L);
        fundOpDto.setBalance(100.0);
        
        Set<ConstraintViolation<FundOpDto>> violations = validator.validate(fundOpDto);
        assertTrue(violations.isEmpty(), "Valid fund operation DTO should have no violations");
    }

    @Test
    void testZeroBalanceValidation() {
        fundOpDto.setOwnerId(1L);
        fundOpDto.setBalance(0.0);
        
        Set<ConstraintViolation<FundOpDto>> violations = validator.validate(fundOpDto);
        assertTrue(violations.size() >= 1, "Zero balance should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Amount must be greater than 0")));
    }

    @Test
    void testNegativeBalanceValidation() {
        fundOpDto.setOwnerId(1L);
        fundOpDto.setBalance(-50.0);
        
        Set<ConstraintViolation<FundOpDto>> violations = validator.validate(fundOpDto);
        assertTrue(violations.size() >= 1, "Negative balance should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Amount must be greater than 0")));
    }

    @Test
    void testZeroOwnerIdValidation() {
        fundOpDto.setOwnerId(0L);
        fundOpDto.setBalance(100.0);
        
        Set<ConstraintViolation<FundOpDto>> violations = validator.validate(fundOpDto);
        assertTrue(violations.size() >= 1, "Zero owner ID should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Owner ID must be a positive number")));
    }

    @Test
    void testNegativeOwnerIdValidation() {
        fundOpDto.setOwnerId(-1L);
        fundOpDto.setBalance(100.0);
        
        Set<ConstraintViolation<FundOpDto>> violations = validator.validate(fundOpDto);
        assertTrue(violations.size() >= 1, "Negative owner ID should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Owner ID must be a positive number")));
    }

    @Test
    void testMinimumValidBalance() {
        fundOpDto.setOwnerId(1L);
        fundOpDto.setBalance(0.01);
        
        Set<ConstraintViolation<FundOpDto>> violations = validator.validate(fundOpDto);
        assertTrue(violations.isEmpty(), "Minimum valid balance should be valid");
    }

    @Test
    void testPositiveOwnerIdValidation() {
        fundOpDto.setOwnerId(1L);
        fundOpDto.setBalance(100.0);
        
        Set<ConstraintViolation<FundOpDto>> violations = validator.validate(fundOpDto);
        assertTrue(violations.isEmpty(), "Positive owner ID should be valid");
    }

    @Test
    void testMultipleValidationErrors() {
        fundOpDto.setOwnerId(-1L);
        fundOpDto.setBalance(0.0);
        
        Set<ConstraintViolation<FundOpDto>> violations = validator.validate(fundOpDto);
        assertTrue(violations.size() >= 2, "Multiple validation errors should be detected");
    }

    @Test
    void testLargeValidValues() {
        fundOpDto.setOwnerId(Long.MAX_VALUE);
        fundOpDto.setBalance(999999.99);
        
        Set<ConstraintViolation<FundOpDto>> violations = validator.validate(fundOpDto);
        assertTrue(violations.isEmpty(), "Large valid values should be valid");
    }

    @Test
    void testDepositScenarioValidation() {
        fundOpDto.setOwnerId(100L);
        fundOpDto.setBalance(500.0);
        
        Set<ConstraintViolation<FundOpDto>> violations = validator.validate(fundOpDto);
        assertTrue(violations.isEmpty(), "Valid deposit scenario should have no violations");
    }

    @Test
    void testWithdrawScenarioValidation() {
        fundOpDto.setOwnerId(200L);
        fundOpDto.setBalance(250.0);
        
        Set<ConstraintViolation<FundOpDto>> violations = validator.validate(fundOpDto);
        assertTrue(violations.isEmpty(), "Valid withdrawal scenario should have no violations");
    }
}
