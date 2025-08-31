package com.exalt_company.kata_bank_api.dto.fund;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OverdrawDtoTest {
    private OverdrawDto overdrawDto;

    @BeforeEach
    void setUp() {
        overdrawDto = new OverdrawDto();
    }

    @Test
    void testOverdrawDtoCreation() {
        Long id = 1L;
        Long ownerId = 123L;
        double maxOverdraw = 500.0;

        overdrawDto.setId(id);
        overdrawDto.setOwnerId(ownerId);
        overdrawDto.setMaxOverdraw(maxOverdraw);

        assertEquals(id, overdrawDto.getId());
        assertEquals(ownerId, overdrawDto.getOwnerId());
        assertEquals(maxOverdraw, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoDefaultValues() {
        assertEquals(0L, overdrawDto.getId());
        assertEquals(0L, overdrawDto.getOwnerId());
        assertEquals(0.0, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithZeroValues() {
        overdrawDto.setId(0L);
        overdrawDto.setOwnerId(0L);
        overdrawDto.setMaxOverdraw(0.0);

        assertEquals(0L, overdrawDto.getId());
        assertEquals(0L, overdrawDto.getOwnerId());
        assertEquals(0.0, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithNegativeValues() {
        overdrawDto.setId(-1L);
        overdrawDto.setOwnerId(-123L);
        overdrawDto.setMaxOverdraw(-500.0);

        assertEquals(-1L, overdrawDto.getId());
        assertEquals(-123L, overdrawDto.getOwnerId());
        assertEquals(-500.0, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithLargeValues() {
        Long largeId = Long.MAX_VALUE;
        Long largeOwnerId = Long.MAX_VALUE;
        double largeMaxOverdraw = Double.MAX_VALUE;

        overdrawDto.setId(largeId);
        overdrawDto.setOwnerId(largeOwnerId);
        overdrawDto.setMaxOverdraw(largeMaxOverdraw);

        assertEquals(largeId, overdrawDto.getId());
        assertEquals(largeOwnerId, overdrawDto.getOwnerId());
        assertEquals(largeMaxOverdraw, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithDecimalMaxOverdraw() {
        double decimalMaxOverdraw = 123.45;

        overdrawDto.setMaxOverdraw(decimalMaxOverdraw);

        assertEquals(decimalMaxOverdraw, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithPrecisionValues() {
        double precisionMaxOverdraw = 100.123456789;

        overdrawDto.setMaxOverdraw(precisionMaxOverdraw);

        assertEquals(precisionMaxOverdraw, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithExtremeValues() {
        Long minId = Long.MIN_VALUE;
        Long minOwnerId = Long.MIN_VALUE;
        double minMaxOverdraw = Double.MIN_VALUE;

        overdrawDto.setId(minId);
        overdrawDto.setOwnerId(minOwnerId);
        overdrawDto.setMaxOverdraw(minMaxOverdraw);

        assertEquals(minId, overdrawDto.getId());
        assertEquals(minOwnerId, overdrawDto.getOwnerId());
        assertEquals(minMaxOverdraw, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithRealisticValues() {
        Long realisticId = 1001L;
        Long realisticOwnerId = 5001L;
        double realisticMaxOverdraw = 1000.0;

        overdrawDto.setId(realisticId);
        overdrawDto.setOwnerId(realisticOwnerId);
        overdrawDto.setMaxOverdraw(realisticMaxOverdraw);

        assertEquals(realisticId, overdrawDto.getId());
        assertEquals(realisticOwnerId, overdrawDto.getOwnerId());
        assertEquals(realisticMaxOverdraw, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithSmallValues() {
        Long smallId = 1L;
        Long smallOwnerId = 1L;
        double smallMaxOverdraw = 0.01;

        overdrawDto.setId(smallId);
        overdrawDto.setOwnerId(smallOwnerId);
        overdrawDto.setMaxOverdraw(smallMaxOverdraw);

        assertEquals(smallId, overdrawDto.getId());
        assertEquals(smallOwnerId, overdrawDto.getOwnerId());
        assertEquals(smallMaxOverdraw, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithMultipleUpdates() {
        overdrawDto.setId(1L);
        overdrawDto.setOwnerId(100L);
        overdrawDto.setMaxOverdraw(500.0);

        overdrawDto.setId(2L);
        overdrawDto.setOwnerId(200L);
        overdrawDto.setMaxOverdraw(750.0);

        assertEquals(2L, overdrawDto.getId());
        assertEquals(200L, overdrawDto.getOwnerId());
        assertEquals(750.0, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithInheritance() {
        overdrawDto.setId(1L);
        overdrawDto.setOwnerId(100L);
        overdrawDto.setMaxOverdraw(500.0);

        assertNotNull(overdrawDto);
        assertEquals(1L, overdrawDto.getId());
        assertEquals(100L, overdrawDto.getOwnerId());
        assertEquals(500.0, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithZeroMaxOverdraw() {
        overdrawDto.setMaxOverdraw(0.0);

        assertEquals(0.0, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithNegativeMaxOverdraw() {
        overdrawDto.setMaxOverdraw(-100.0);

        assertEquals(-100.0, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithLargeMaxOverdraw() {
        double largeMaxOverdraw = 1000000.0;

        overdrawDto.setMaxOverdraw(largeMaxOverdraw);

        assertEquals(largeMaxOverdraw, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithScientificNotation() {
        double scientificMaxOverdraw = 1.23e5;

        overdrawDto.setMaxOverdraw(scientificMaxOverdraw);

        assertEquals(scientificMaxOverdraw, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithInfinityValues() {
        overdrawDto.setMaxOverdraw(Double.POSITIVE_INFINITY);

        assertEquals(Double.POSITIVE_INFINITY, overdrawDto.getMaxOverdraw());

        overdrawDto.setMaxOverdraw(Double.NEGATIVE_INFINITY);

        assertEquals(Double.NEGATIVE_INFINITY, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithNaNValue() {
        overdrawDto.setMaxOverdraw(Double.NaN);

        assertTrue(Double.isNaN(overdrawDto.getMaxOverdraw()));
    }

    @Test
    void testOverdrawDtoWithMultipleDecimalPlaces() {
        double multiDecimalMaxOverdraw = 123.456789;

        overdrawDto.setMaxOverdraw(multiDecimalMaxOverdraw);

        assertEquals(multiDecimalMaxOverdraw, overdrawDto.getMaxOverdraw());
    }

    @Test
    void testOverdrawDtoWithCurrencyValues() {
        double currencyMaxOverdraw = 1234.56;

        overdrawDto.setMaxOverdraw(currencyMaxOverdraw);

        assertEquals(currencyMaxOverdraw, overdrawDto.getMaxOverdraw());
    }
}
