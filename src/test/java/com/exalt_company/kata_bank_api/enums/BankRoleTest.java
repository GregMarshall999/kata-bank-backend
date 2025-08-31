package com.exalt_company.kata_bank_api.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankRoleTest {
    @Test
    void testBankRoleValues() {
        BankRole[] roles = BankRole.values();
        
        assertEquals(3, roles.length);
        assertTrue(containsRole(roles, BankRole.ADMIN));
        assertTrue(containsRole(roles, BankRole.ADVISOR));
        assertTrue(containsRole(roles, BankRole.CLIENT));
    }

    @Test
    void testBankRoleValueOf() {
        assertEquals(BankRole.ADMIN, BankRole.valueOf("ADMIN"));
        assertEquals(BankRole.ADVISOR, BankRole.valueOf("ADVISOR"));
        assertEquals(BankRole.CLIENT, BankRole.valueOf("CLIENT"));
    }

    @Test
    void testBankRoleValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            BankRole.valueOf("INVALID_ROLE");
        });
    }

    @Test
    void testBankRoleOrdinal() {
        assertEquals(0, BankRole.ADMIN.ordinal());
        assertEquals(1, BankRole.ADVISOR.ordinal());
        assertEquals(2, BankRole.CLIENT.ordinal());
    }

    @Test
    void testBankRoleName() {
        assertEquals("ADMIN", BankRole.ADMIN.name());
        assertEquals("ADVISOR", BankRole.ADVISOR.name());
        assertEquals("CLIENT", BankRole.CLIENT.name());
    }

    @Test
    void testBankRoleToString() {
        assertEquals("ADMIN", BankRole.ADMIN.toString());
        assertEquals("ADVISOR", BankRole.ADVISOR.toString());
        assertEquals("CLIENT", BankRole.CLIENT.toString());
    }

    @Test
    void testBankRoleInequality() {
        assertNotEquals(BankRole.ADMIN, BankRole.ADVISOR);
        assertNotEquals(BankRole.ADMIN, BankRole.CLIENT);
        assertNotEquals(BankRole.ADVISOR, BankRole.CLIENT);
    }

    @Test
    void testBankRoleHashCode() {
        assertEquals(BankRole.ADMIN.hashCode(), BankRole.ADMIN.hashCode());
        assertEquals(BankRole.ADVISOR.hashCode(), BankRole.ADVISOR.hashCode());
        assertEquals(BankRole.CLIENT.hashCode(), BankRole.CLIENT.hashCode());
        
        assertNotEquals(BankRole.ADMIN.hashCode(), BankRole.ADVISOR.hashCode());
        assertNotEquals(BankRole.ADMIN.hashCode(), BankRole.CLIENT.hashCode());
    }

    @Test
    void testBankRoleCompareTo() {
        assertTrue(BankRole.ADMIN.compareTo(BankRole.ADVISOR) < 0);
        assertTrue(BankRole.ADMIN.compareTo(BankRole.CLIENT) < 0);
        assertTrue(BankRole.ADVISOR.compareTo(BankRole.CLIENT) < 0);
        
        assertTrue(BankRole.ADVISOR.compareTo(BankRole.ADMIN) > 0);
        assertTrue(BankRole.CLIENT.compareTo(BankRole.ADMIN) > 0);
        assertTrue(BankRole.CLIENT.compareTo(BankRole.ADVISOR) > 0);
    }

    private boolean containsRole(BankRole[] roles, BankRole role) {
        for (BankRole r : roles) {
            if (r == role) {
                return true;
            }
        }
        return false;
    }
} 