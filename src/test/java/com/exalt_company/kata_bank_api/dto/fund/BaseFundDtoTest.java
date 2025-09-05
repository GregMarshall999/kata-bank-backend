package com.exalt_company.kata_bank_api.dto.fund;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BaseFundDtoTest {
    private BaseFundDto baseFundDto;

    @BeforeEach
    void setUp() {
        baseFundDto = new BaseFundDto() {};
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(baseFundDto);
        assertEquals(0L, baseFundDto.getId());
        assertEquals(0L, baseFundDto.getOwnerId());
    }

    @Test
    void testSetAndGetOwnerId() {
        long ownerId = 123L;
        
        baseFundDto.setOwnerId(ownerId);
        
        assertEquals(ownerId, baseFundDto.getOwnerId());
    }

    @Test
    void testInheritedIdField() {
        long testId = 456L;
        
        baseFundDto.setId(testId);
        
        assertEquals(testId, baseFundDto.getId());
    }

    @Test
    void testOwnerIdWithZero() {
        baseFundDto.setOwnerId(0L);
        
        assertEquals(0L, baseFundDto.getOwnerId());
    }

    @Test
    void testOwnerIdWithNegativeValue() {
        long negativeOwnerId = -1L;
        
        baseFundDto.setOwnerId(negativeOwnerId);
        
        assertEquals(negativeOwnerId, baseFundDto.getOwnerId());
    }

    @Test
    void testOwnerIdWithMaxValue() {
        long maxOwnerId = Long.MAX_VALUE;
        
        baseFundDto.setOwnerId(maxOwnerId);
        
        assertEquals(maxOwnerId, baseFundDto.getOwnerId());
    }

    @Test
    void testOwnerIdWithMinValue() {
        long minOwnerId = Long.MIN_VALUE;
        
        baseFundDto.setOwnerId(minOwnerId);
        
        assertEquals(minOwnerId, baseFundDto.getOwnerId());
    }

    @Test
    void testMultipleOwnerIdChanges() {
        baseFundDto.setOwnerId(1L);
        assertEquals(1L, baseFundDto.getOwnerId());
        
        baseFundDto.setOwnerId(100L);
        assertEquals(100L, baseFundDto.getOwnerId());
        
        baseFundDto.setOwnerId(999L);
        assertEquals(999L, baseFundDto.getOwnerId());
    }

    @Test
    void testOwnerIdWithLargeNumbers() {
        long largeOwnerId = 999999999L;
        
        baseFundDto.setOwnerId(largeOwnerId);
        
        assertEquals(largeOwnerId, baseFundDto.getOwnerId());
    }

    @Test
    void testOwnerIdWithSmallNumbers() {
        long smallOwnerId = 1L;
        
        baseFundDto.setOwnerId(smallOwnerId);
        
        assertEquals(smallOwnerId, baseFundDto.getOwnerId());
    }

    @Test
    void testBothIdAndOwnerId() {
        long id = 789L;
        long ownerId = 123L;
        
        baseFundDto.setId(id);
        baseFundDto.setOwnerId(ownerId);
        
        assertEquals(id, baseFundDto.getId());
        assertEquals(ownerId, baseFundDto.getOwnerId());
    }

    @Test
    void testRealisticBankingScenario() {
        long fundId = 1001L;
        long userId = 5001L;
        
        baseFundDto.setId(fundId);
        baseFundDto.setOwnerId(userId);
        
        assertEquals(fundId, baseFundDto.getId());
        assertEquals(userId, baseFundDto.getOwnerId());
    }

    @Test
    void testOwnerIdWithDifferentValues() {
        long[] ownerIds = {1L, 10L, 100L, 1000L, 10000L, 100000L};
        
        for (long ownerId : ownerIds) {
            baseFundDto.setOwnerId(ownerId);
            assertEquals(ownerId, baseFundDto.getOwnerId());
        }
    }

    @Test
    void testOwnerIdIndependenceFromId() {
        long id = 1L;
        long ownerId = 2L;
        
        baseFundDto.setId(id);
        baseFundDto.setOwnerId(ownerId);
        
        assertEquals(id, baseFundDto.getId());
        assertEquals(ownerId, baseFundDto.getOwnerId());
        
        baseFundDto.setOwnerId(3L);
        
        assertEquals(id, baseFundDto.getId());
        assertEquals(3L, baseFundDto.getOwnerId());
    }
}
