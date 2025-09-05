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

class BaseDtoTest {
    private BaseDto baseDto;
    private Validator validator;

    @BeforeEach
    void setUp() {
        baseDto = new BaseDto() {};
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(baseDto);
        assertEquals(0L, baseDto.getId());
    }

    @Test
    void testSetAndGetId() {
        long testId = 123L;
        
        baseDto.setId(testId);
        
        assertEquals(testId, baseDto.getId());
    }

    @Test
    void testSetAndGetIdWithZero() {
        baseDto.setId(0L);
        
        assertEquals(0L, baseDto.getId());
    }

    @Test
    void testSetAndGetIdWithNegativeValue() {
        long negativeId = -1L;
        
        baseDto.setId(negativeId);
        
        assertEquals(negativeId, baseDto.getId());
    }

    @Test
    void testSetAndGetIdWithMaxValue() {
        long maxId = Long.MAX_VALUE;
        
        baseDto.setId(maxId);
        
        assertEquals(maxId, baseDto.getId());
    }

    @Test
    void testSetAndGetIdWithMinValue() {
        long minId = Long.MIN_VALUE;
        
        baseDto.setId(minId);
        
        assertEquals(minId, baseDto.getId());
    }

    @Test
    void testMultipleIdChanges() {
        baseDto.setId(1L);
        assertEquals(1L, baseDto.getId());
        
        baseDto.setId(100L);
        assertEquals(100L, baseDto.getId());
        
        baseDto.setId(999L);
        assertEquals(999L, baseDto.getId());
    }

    @Test
    void testIdWithLargeNumbers() {
        long largeId = 999999999L;
        
        baseDto.setId(largeId);
        
        assertEquals(largeId, baseDto.getId());
    }

    @Test
    void testIdWithSmallNumbers() {
        long smallId = 1L;
        
        baseDto.setId(smallId);
        
        assertEquals(smallId, baseDto.getId());
    }

    @Test
    void testIdSerialization() {
        assertNotNull(baseDto);
        assertEquals(0L, baseDto.getId());
        
        baseDto.setId(42L);
        assertEquals(42L, baseDto.getId());
    }

    // Validation Tests
    @Test
    void testValidIdWithZero() {
        baseDto.setId(0L);
        
        Set<ConstraintViolation<BaseDto>> violations = validator.validate(baseDto);
        assertTrue(violations.isEmpty(), "Zero ID should be valid");
    }

    @Test
    void testValidIdWithPositiveValue() {
        baseDto.setId(1L);
        
        Set<ConstraintViolation<BaseDto>> violations = validator.validate(baseDto);
        assertTrue(violations.isEmpty(), "Positive ID should be valid");
    }

    @Test
    void testInvalidIdWithNegativeValue() {
        baseDto.setId(-1L);
        
        Set<ConstraintViolation<BaseDto>> violations = validator.validate(baseDto);
        assertTrue(violations.size() >= 1, "Negative ID should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("ID must be a non-negative number")));
    }

    @Test
    void testValidIdWithLargeValue() {
        baseDto.setId(Long.MAX_VALUE);
        
        Set<ConstraintViolation<BaseDto>> violations = validator.validate(baseDto);
        assertTrue(violations.isEmpty(), "Large positive ID should be valid");
    }
}
