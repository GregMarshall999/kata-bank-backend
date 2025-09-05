package com.exalt_company.kata_bank_api.dto;

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

class SavingDtoTest {
    private SavingDto savingDto;
    private Validator validator;

    @BeforeEach
    void setUp() {
        savingDto = new SavingDto();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(savingDto);
        assertEquals(0L, savingDto.getId());
        assertEquals(0.0, savingDto.getBalance(), 0.001);
        assertEquals(0.0, savingDto.getMaxBalance(), 0.001);
        assertEquals(0L, savingDto.getOwnerId());
    }

    @Test
    void testSetAndGetBalance() {
        double balance = 1000.50;
        
        savingDto.setBalance(balance);
        
        assertEquals(balance, savingDto.getBalance(), 0.001);
    }

    @Test
    void testSetAndGetMaxBalance() {
        double maxBalance = 10000.0;
        
        savingDto.setMaxBalance(maxBalance);
        
        assertEquals(maxBalance, savingDto.getMaxBalance(), 0.001);
    }

    @Test
    void testSetAndGetOwnerId() {
        long ownerId = 123L;
        
        savingDto.setOwnerId(ownerId);
        
        assertEquals(ownerId, savingDto.getOwnerId());
    }

    @Test
    void testAllFieldsTogether() {
        long id = 1L;
        double balance = 2500.75;
        double maxBalance = 50000.0;
        long ownerId = 456L;
        
        savingDto.setId(id);
        savingDto.setBalance(balance);
        savingDto.setMaxBalance(maxBalance);
        savingDto.setOwnerId(ownerId);
        
        assertEquals(id, savingDto.getId());
        assertEquals(balance, savingDto.getBalance(), 0.001);
        assertEquals(maxBalance, savingDto.getMaxBalance(), 0.001);
        assertEquals(ownerId, savingDto.getOwnerId());
    }

    @Test
    void testZeroValues() {
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(0.0);
        savingDto.setOwnerId(0L);
        
        assertEquals(0.0, savingDto.getBalance(), 0.001);
        assertEquals(0.0, savingDto.getMaxBalance(), 0.001);
        assertEquals(0L, savingDto.getOwnerId());
    }

    @Test
    void testNegativeValues() {
        double negativeBalance = -100.0;
        double negativeMaxBalance = -500.0;
        long negativeOwnerId = -1L;
        
        savingDto.setBalance(negativeBalance);
        savingDto.setMaxBalance(negativeMaxBalance);
        savingDto.setOwnerId(negativeOwnerId);
        
        assertEquals(negativeBalance, savingDto.getBalance(), 0.001);
        assertEquals(negativeMaxBalance, savingDto.getMaxBalance(), 0.001);
        assertEquals(negativeOwnerId, savingDto.getOwnerId());
    }

    @Test
    void testLargeValues() {
        double largeBalance = 999999999.99;
        double largeMaxBalance = Double.MAX_VALUE;
        long largeOwnerId = Long.MAX_VALUE;
        
        savingDto.setBalance(largeBalance);
        savingDto.setMaxBalance(largeMaxBalance);
        savingDto.setOwnerId(largeOwnerId);
        
        assertEquals(largeBalance, savingDto.getBalance(), 0.001);
        assertEquals(largeMaxBalance, savingDto.getMaxBalance(), 0.001);
        assertEquals(largeOwnerId, savingDto.getOwnerId());
    }

    @Test
    void testDecimalPrecision() {
        double preciseBalance = 1234.56789;
        double preciseMaxBalance = 9876.54321;
        
        savingDto.setBalance(preciseBalance);
        savingDto.setMaxBalance(preciseMaxBalance);
        
        assertEquals(preciseBalance, savingDto.getBalance(), 0.001);
        assertEquals(preciseMaxBalance, savingDto.getMaxBalance(), 0.001);
    }

    @Test
    void testScientificNotation() {
        double scientificBalance = 1.23e5;
        double scientificMaxBalance = 5.67e6;
        
        savingDto.setBalance(scientificBalance);
        savingDto.setMaxBalance(scientificMaxBalance);
        
        assertEquals(scientificBalance, savingDto.getBalance(), 0.001);
        assertEquals(scientificMaxBalance, savingDto.getMaxBalance(), 0.001);
    }

    @Test
    void testInheritedIdField() {
        long testId = 789L;
        
        savingDto.setId(testId);
        
        assertEquals(testId, savingDto.getId());
    }

    @Test
    void testMultipleUpdates() {
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(100L);
        
        savingDto.setBalance(2000.0);
        savingDto.setMaxBalance(20000.0);
        savingDto.setOwnerId(200L);
        
        assertEquals(2000.0, savingDto.getBalance(), 0.001);
        assertEquals(20000.0, savingDto.getMaxBalance(), 0.001);
        assertEquals(200L, savingDto.getOwnerId());
    }

    @Test
    void testBalanceEqualToMaxBalance() {
        double sameValue = 5000.0;
        
        savingDto.setBalance(sameValue);
        savingDto.setMaxBalance(sameValue);
        
        assertEquals(sameValue, savingDto.getBalance(), 0.001);
        assertEquals(sameValue, savingDto.getMaxBalance(), 0.001);
    }

    @Test
    void testBalanceGreaterThanMaxBalance() {
        double balance = 10000.0;
        double maxBalance = 5000.0;
        
        savingDto.setBalance(balance);
        savingDto.setMaxBalance(maxBalance);
        
        assertEquals(balance, savingDto.getBalance(), 0.001);
        assertEquals(maxBalance, savingDto.getMaxBalance(), 0.001);
    }

    @Test
    void testSmallDecimalValues() {
        double smallBalance = 0.01;
        double smallMaxBalance = 0.02;
        
        savingDto.setBalance(smallBalance);
        savingDto.setMaxBalance(smallMaxBalance);
        
        assertEquals(smallBalance, savingDto.getBalance(), 0.001);
        assertEquals(smallMaxBalance, savingDto.getMaxBalance(), 0.001);
    }

    @Test
    void testCurrencyLikeValues() {
        double currencyBalance = 1234.56;
        double currencyMaxBalance = 9876.54;
        
        savingDto.setBalance(currencyBalance);
        savingDto.setMaxBalance(currencyMaxBalance);
        
        assertEquals(currencyBalance, savingDto.getBalance(), 0.001);
        assertEquals(currencyMaxBalance, savingDto.getMaxBalance(), 0.001);
    }

    @Test
    void testOwnerIdWithDifferentValues() {
        long[] ownerIds = {1L, 100L, 999L, 1000L, Long.MAX_VALUE, Long.MIN_VALUE};
        
        for (long ownerId : ownerIds) {
            savingDto.setOwnerId(ownerId);
            assertEquals(ownerId, savingDto.getOwnerId());
        }
    }

    @Test
    void testRealisticBankingScenario() {
        long id = 1001L;
        double balance = 15000.75;
        double maxBalance = 100000.0;
        long ownerId = 5001L;
        
        savingDto.setId(id);
        savingDto.setBalance(balance);
        savingDto.setMaxBalance(maxBalance);
        savingDto.setOwnerId(ownerId);
        
        assertEquals(id, savingDto.getId());
        assertEquals(balance, savingDto.getBalance(), 0.001);
        assertEquals(maxBalance, savingDto.getMaxBalance(), 0.001);
        assertEquals(ownerId, savingDto.getOwnerId());
    }

    @Test
    void testInfinityValues() {
        savingDto.setBalance(Double.POSITIVE_INFINITY);
        savingDto.setMaxBalance(Double.NEGATIVE_INFINITY);
        
        assertEquals(Double.POSITIVE_INFINITY, savingDto.getBalance());
        assertEquals(Double.NEGATIVE_INFINITY, savingDto.getMaxBalance());
    }

    @Test
    void testNaNValues() {
        savingDto.setBalance(Double.NaN);
        savingDto.setMaxBalance(Double.NaN);
        
        assertEquals(Double.NaN, savingDto.getBalance());
        assertEquals(Double.NaN, savingDto.getMaxBalance());
    }

    // Validation Tests
    @Test
    void testValidSavingDto() {
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(1L);
        
        Set<ConstraintViolation<SavingDto>> violations = validator.validate(savingDto);
        assertTrue(violations.isEmpty(), "Valid saving DTO should have no violations");
    }

    @Test
    void testNegativeBalanceValidation() {
        savingDto.setBalance(-100.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(1L);
        
        Set<ConstraintViolation<SavingDto>> violations = validator.validate(savingDto);
        assertTrue(violations.size() >= 1, "Negative balance should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Balance must be non-negative")));
    }

    @Test
    void testZeroBalanceValidation() {
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(1L);
        
        Set<ConstraintViolation<SavingDto>> violations = validator.validate(savingDto);
        assertTrue(violations.isEmpty(), "Zero balance should be valid");
    }

    @Test
    void testZeroMaxBalanceValidation() {
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(0.0);
        savingDto.setOwnerId(1L);
        
        Set<ConstraintViolation<SavingDto>> violations = validator.validate(savingDto);
        assertTrue(violations.size() >= 1, "Zero max balance should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Maximum balance must be greater than 0")));
    }

    @Test
    void testNegativeMaxBalanceValidation() {
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(-100.0);
        savingDto.setOwnerId(1L);
        
        Set<ConstraintViolation<SavingDto>> violations = validator.validate(savingDto);
        assertTrue(violations.size() >= 1, "Negative max balance should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Maximum balance must be greater than 0")));
    }

    @Test
    void testZeroOwnerIdValidation() {
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(0L);
        
        Set<ConstraintViolation<SavingDto>> violations = validator.validate(savingDto);
        assertTrue(violations.size() >= 1, "Zero owner ID should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Owner ID must be a positive number")));
    }

    @Test
    void testNegativeOwnerIdValidation() {
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(-1L);
        
        Set<ConstraintViolation<SavingDto>> violations = validator.validate(savingDto);
        assertTrue(violations.size() >= 1, "Negative owner ID should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Owner ID must be a positive number")));
    }

    @Test
    void testMaxBalanceMinimumValue() {
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(0.01); // Minimum valid value
        savingDto.setOwnerId(1L);
        
        Set<ConstraintViolation<SavingDto>> violations = validator.validate(savingDto);
        assertTrue(violations.isEmpty(), "Minimum valid max balance should be valid");
    }

    @Test
    void testPositiveOwnerIdValidation() {
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(1L);
        
        Set<ConstraintViolation<SavingDto>> violations = validator.validate(savingDto);
        assertTrue(violations.isEmpty(), "Positive owner ID should be valid");
    }

    @Test
    void testMultipleValidationErrors() {
        savingDto.setBalance(-100.0); // Negative balance
        savingDto.setMaxBalance(0.0); // Zero max balance
        savingDto.setOwnerId(-1L); // Negative owner ID
        
        Set<ConstraintViolation<SavingDto>> violations = validator.validate(savingDto);
        assertTrue(violations.size() >= 3, "Multiple validation errors should be detected");
    }

    @Test
    void testLargeValidValues() {
        savingDto.setBalance(999999.99);
        savingDto.setMaxBalance(9999999.99);
        savingDto.setOwnerId(Long.MAX_VALUE);
        
        Set<ConstraintViolation<SavingDto>> violations = validator.validate(savingDto);
        assertTrue(violations.isEmpty(), "Large valid values should be valid");
    }
}
