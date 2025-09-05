package com.exalt_company.kata_bank_api.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BaseEntityTest {
    private BaseEntity baseEntity;

    @BeforeEach
    void setUp() {
        baseEntity = new BaseEntity() {};
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(baseEntity);
        assertEquals(0L, baseEntity.getId());
        assertEquals(0, baseEntity.getVersion());
        assertNull(baseEntity.getCreatedAt());
        assertNull(baseEntity.getUpdatedAt());
    }

    @Test
    void testSetAndGetId() {
        long testId = 123L;
        
        baseEntity.setId(testId);
        
        assertEquals(testId, baseEntity.getId());
    }

    @Test
    void testSetAndGetVersion() {
        int testVersion = 5;
        
        baseEntity.setVersion(testVersion);
        
        assertEquals(testVersion, baseEntity.getVersion());
    }

    @Test
    void testIdWithZero() {
        baseEntity.setId(0L);
        
        assertEquals(0L, baseEntity.getId());
    }

    @Test
    void testIdWithNegativeValue() {
        long negativeId = -1L;
        
        baseEntity.setId(negativeId);
        
        assertEquals(negativeId, baseEntity.getId());
    }

    @Test
    void testIdWithMaxValue() {
        long maxId = Long.MAX_VALUE;
        
        baseEntity.setId(maxId);
        
        assertEquals(maxId, baseEntity.getId());
    }

    @Test
    void testIdWithMinValue() {
        long minId = Long.MIN_VALUE;
        
        baseEntity.setId(minId);
        
        assertEquals(minId, baseEntity.getId());
    }

    @Test
    void testVersionWithZero() {
        baseEntity.setVersion(0);
        
        assertEquals(0, baseEntity.getVersion());
    }

    @Test
    void testVersionWithNegativeValue() {
        int negativeVersion = -1;
        
        baseEntity.setVersion(negativeVersion);
        
        assertEquals(negativeVersion, baseEntity.getVersion());
    }

    @Test
    void testVersionWithMaxValue() {
        int maxVersion = Integer.MAX_VALUE;
        
        baseEntity.setVersion(maxVersion);
        
        assertEquals(maxVersion, baseEntity.getVersion());
    }

    @Test
    void testVersionWithMinValue() {
        int minVersion = Integer.MIN_VALUE;
        
        baseEntity.setVersion(minVersion);
        
        assertEquals(minVersion, baseEntity.getVersion());
    }

    @Test
    void testMultipleIdChanges() {
        baseEntity.setId(1L);
        assertEquals(1L, baseEntity.getId());
        
        baseEntity.setId(100L);
        assertEquals(100L, baseEntity.getId());
        
        baseEntity.setId(999L);
        assertEquals(999L, baseEntity.getId());
    }

    @Test
    void testMultipleVersionChanges() {
        baseEntity.setVersion(1);
        assertEquals(1, baseEntity.getVersion());
        
        baseEntity.setVersion(10);
        assertEquals(10, baseEntity.getVersion());
        
        baseEntity.setVersion(99);
        assertEquals(99, baseEntity.getVersion());
    }

    @Test
    void testIdWithLargeNumbers() {
        long largeId = 999999999L;
        
        baseEntity.setId(largeId);
        
        assertEquals(largeId, baseEntity.getId());
    }

    @Test
    void testVersionWithLargeNumbers() {
        int largeVersion = 999999;
        
        baseEntity.setVersion(largeVersion);
        
        assertEquals(largeVersion, baseEntity.getVersion());
    }

    @Test
    void testIdWithSmallNumbers() {
        long smallId = 1L;
        
        baseEntity.setId(smallId);
        
        assertEquals(smallId, baseEntity.getId());
    }

    @Test
    void testVersionWithSmallNumbers() {
        int smallVersion = 1;
        
        baseEntity.setVersion(smallVersion);
        
        assertEquals(smallVersion, baseEntity.getVersion());
    }

    @Test
    void testBothIdAndVersion() {
        long id = 789L;
        int version = 42;
        
        baseEntity.setId(id);
        baseEntity.setVersion(version);
        
        assertEquals(id, baseEntity.getId());
        assertEquals(version, baseEntity.getVersion());
    }

    @Test
    void testRealisticEntityScenario() {
        long entityId = 1001L;
        int entityVersion = 3;
        
        baseEntity.setId(entityId);
        baseEntity.setVersion(entityVersion);
        
        assertEquals(entityId, baseEntity.getId());
        assertEquals(entityVersion, baseEntity.getVersion());
    }

    @Test
    void testIdIndependenceFromVersion() {
        long id = 1L;
        int version = 2;
        
        baseEntity.setId(id);
        baseEntity.setVersion(version);
        
        assertEquals(id, baseEntity.getId());
        assertEquals(version, baseEntity.getVersion());
        
        baseEntity.setId(3L);
        
        assertEquals(3L, baseEntity.getId());
        assertEquals(version, baseEntity.getVersion());
    }

    @Test
    void testVersionIndependenceFromId() {
        long id = 1L;
        int version = 2;
        
        baseEntity.setId(id);
        baseEntity.setVersion(version);
        
        assertEquals(id, baseEntity.getId());
        assertEquals(version, baseEntity.getVersion());
        
        baseEntity.setVersion(4);
        
        assertEquals(id, baseEntity.getId());
        assertEquals(4, baseEntity.getVersion());
    }

    @Test
    void testSerialization() {
        assertNotNull(baseEntity);
        assertEquals(0L, baseEntity.getId());
        assertEquals(0, baseEntity.getVersion());
        
        baseEntity.setId(42L);
        baseEntity.setVersion(7);
        
        assertEquals(42L, baseEntity.getId());
        assertEquals(7, baseEntity.getVersion());
    }

    @Test
    void testTimestampFieldsInitialState() {
        assertNull(baseEntity.getCreatedAt());
        assertNull(baseEntity.getUpdatedAt());
    }

    @Test
    void testEntityWithDifferentValues() {
        long[] ids = {1L, 10L, 100L, 1000L, 10000L, 100000L};
        int[] versions = {1, 10, 100, 1000, 10000, 100000};
        
        for (long id : ids) {
            baseEntity.setId(id);
            assertEquals(id, baseEntity.getId());
        }
        
        for (int version : versions) {
            baseEntity.setVersion(version);
            assertEquals(version, baseEntity.getVersion());
        }
    }

    @Test
    void testEntityWithExtremeValues() {
        baseEntity.setId(Long.MAX_VALUE);
        baseEntity.setVersion(Integer.MAX_VALUE);
        
        assertEquals(Long.MAX_VALUE, baseEntity.getId());
        assertEquals(Integer.MAX_VALUE, baseEntity.getVersion());
        
        baseEntity.setId(Long.MIN_VALUE);
        baseEntity.setVersion(Integer.MIN_VALUE);
        
        assertEquals(Long.MIN_VALUE, baseEntity.getId());
        assertEquals(Integer.MIN_VALUE, baseEntity.getVersion());
    }
}
