package com.exalt_company.kata_bank_api.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditOperationTest {

    @Test
    void testEnumValues() {
        AuditOperation[] operations = AuditOperation.values();

        assertNotNull(operations);
        assertEquals(6, operations.length);
        
        assertTrue(contains(operations, AuditOperation.CLOSE));
        assertTrue(contains(operations, AuditOperation.DEPOSIT));
        assertTrue(contains(operations, AuditOperation.OPEN));
        assertTrue(contains(operations, AuditOperation.OVERDRAW_CANCEL));
        assertTrue(contains(operations, AuditOperation.OVERDRAW_REQUEST));
        assertTrue(contains(operations, AuditOperation.WITHDRAW));
    }

    @Test
    void testValueOf() {
        assertEquals(AuditOperation.CLOSE, AuditOperation.valueOf("CLOSE"));
        assertEquals(AuditOperation.DEPOSIT, AuditOperation.valueOf("DEPOSIT"));
        assertEquals(AuditOperation.OPEN, AuditOperation.valueOf("OPEN"));
        assertEquals(AuditOperation.OVERDRAW_CANCEL, AuditOperation.valueOf("OVERDRAW_CANCEL"));
        assertEquals(AuditOperation.OVERDRAW_REQUEST, AuditOperation.valueOf("OVERDRAW_REQUEST"));
        assertEquals(AuditOperation.WITHDRAW, AuditOperation.valueOf("WITHDRAW"));
    }

    @Test
    void testValueOf_InvalidValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> AuditOperation.valueOf("INVALID_OPERATION"));
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, AuditOperation.CLOSE.ordinal());
        assertEquals(1, AuditOperation.DEPOSIT.ordinal());
        assertEquals(2, AuditOperation.OPEN.ordinal());
        assertEquals(3, AuditOperation.OVERDRAW_CANCEL.ordinal());
        assertEquals(4, AuditOperation.OVERDRAW_REQUEST.ordinal());
        assertEquals(5, AuditOperation.WITHDRAW.ordinal());
    }

    @Test
    void testEnumName() {
        assertEquals("CLOSE", AuditOperation.CLOSE.name());
        assertEquals("DEPOSIT", AuditOperation.DEPOSIT.name());
        assertEquals("OPEN", AuditOperation.OPEN.name());
        assertEquals("OVERDRAW_CANCEL", AuditOperation.OVERDRAW_CANCEL.name());
        assertEquals("OVERDRAW_REQUEST", AuditOperation.OVERDRAW_REQUEST.name());
        assertEquals("WITHDRAW", AuditOperation.WITHDRAW.name());
    }

    @Test
    void testEnumToString() {
        assertEquals("CLOSE", AuditOperation.CLOSE.toString());
        assertEquals("DEPOSIT", AuditOperation.DEPOSIT.toString());
        assertEquals("OPEN", AuditOperation.OPEN.toString());
        assertEquals("OVERDRAW_CANCEL", AuditOperation.OVERDRAW_CANCEL.toString());
        assertEquals("OVERDRAW_REQUEST", AuditOperation.OVERDRAW_REQUEST.toString());
        assertEquals("WITHDRAW", AuditOperation.WITHDRAW.toString());
    }

    @Test
    void testEnumHashCode() {
        assertEquals(AuditOperation.CLOSE.hashCode(), AuditOperation.CLOSE.hashCode());
        assertEquals(AuditOperation.DEPOSIT.hashCode(), AuditOperation.DEPOSIT.hashCode());
        assertEquals(AuditOperation.OPEN.hashCode(), AuditOperation.OPEN.hashCode());
        assertEquals(AuditOperation.OVERDRAW_CANCEL.hashCode(), AuditOperation.OVERDRAW_CANCEL.hashCode());
        assertEquals(AuditOperation.OVERDRAW_REQUEST.hashCode(), AuditOperation.OVERDRAW_REQUEST.hashCode());
        assertEquals(AuditOperation.WITHDRAW.hashCode(), AuditOperation.WITHDRAW.hashCode());
        
        assertNotEquals(AuditOperation.CLOSE.hashCode(), AuditOperation.DEPOSIT.hashCode());
        assertNotEquals(AuditOperation.OPEN.hashCode(), AuditOperation.WITHDRAW.hashCode());
    }

    @Test
    void testEnumGetDeclaringClass() {
        assertEquals(AuditOperation.class, AuditOperation.CLOSE.getDeclaringClass());
        assertEquals(AuditOperation.class, AuditOperation.DEPOSIT.getDeclaringClass());
        assertEquals(AuditOperation.class, AuditOperation.OPEN.getDeclaringClass());
        assertEquals(AuditOperation.class, AuditOperation.OVERDRAW_CANCEL.getDeclaringClass());
        assertEquals(AuditOperation.class, AuditOperation.OVERDRAW_REQUEST.getDeclaringClass());
        assertEquals(AuditOperation.class, AuditOperation.WITHDRAW.getDeclaringClass());
    }

    @Test
    void testEnumGetClass() {
        assertSame(AuditOperation.class, AuditOperation.CLOSE.getClass());
        assertSame(AuditOperation.class, AuditOperation.DEPOSIT.getClass());
        assertSame(AuditOperation.class, AuditOperation.OPEN.getClass());
        assertSame(AuditOperation.class, AuditOperation.OVERDRAW_CANCEL.getClass());
        assertSame(AuditOperation.class, AuditOperation.OVERDRAW_REQUEST.getClass());
        assertSame(AuditOperation.class, AuditOperation.WITHDRAW.getClass());
    }

    @Test
    void testEnumSwitchStatement() {
        assertEquals("Close operation", getOperationDescription(AuditOperation.CLOSE));
        assertEquals("Deposit operation", getOperationDescription(AuditOperation.DEPOSIT));
        assertEquals("Open operation", getOperationDescription(AuditOperation.OPEN));
        assertEquals("Overdraw cancel operation", getOperationDescription(AuditOperation.OVERDRAW_CANCEL));
        assertEquals("Overdraw request operation", getOperationDescription(AuditOperation.OVERDRAW_REQUEST));
        assertEquals("Withdraw operation", getOperationDescription(AuditOperation.WITHDRAW));
    }

    @Test
    void testEnumInCollections() {
        java.util.Set<AuditOperation> operationSet = new java.util.HashSet<>();
        java.util.List<AuditOperation> operationList = new java.util.ArrayList<>();
        
        operationSet.add(AuditOperation.CLOSE);
        operationSet.add(AuditOperation.DEPOSIT);
        operationSet.add(AuditOperation.OPEN);
        
        operationList.add(AuditOperation.WITHDRAW);
        operationList.add(AuditOperation.OVERDRAW_REQUEST);
        operationList.add(AuditOperation.OVERDRAW_CANCEL);
        
        assertEquals(3, operationSet.size());
        assertTrue(operationSet.contains(AuditOperation.CLOSE));
        assertTrue(operationSet.contains(AuditOperation.DEPOSIT));
        assertTrue(operationSet.contains(AuditOperation.OPEN));
        
        assertEquals(3, operationList.size());
        assertEquals(AuditOperation.WITHDRAW, operationList.get(0));
        assertEquals(AuditOperation.OVERDRAW_REQUEST, operationList.get(1));
        assertEquals(AuditOperation.OVERDRAW_CANCEL, operationList.get(2));
    }

    private String getOperationDescription(AuditOperation operation) {
        return switch (operation) {
            case CLOSE -> "Close operation";
            case DEPOSIT -> "Deposit operation";
            case OPEN -> "Open operation";
            case OVERDRAW_CANCEL -> "Overdraw cancel operation";
            case OVERDRAW_REQUEST -> "Overdraw request operation";
            case WITHDRAW -> "Withdraw operation";
        };
    }

    private boolean contains(AuditOperation[] operations, AuditOperation operation) {
        for (AuditOperation op : operations) {
            if (op == operation) {
                return true;
            }
        }
        return false;
    }
}
