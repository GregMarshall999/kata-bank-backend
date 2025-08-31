package com.exalt_company.kata_bank_api.entity.user_fields;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IdentityTest {
    private Identity identity;

    @BeforeEach
    void setUp() {
        identity = new Identity();
    }

    @Test
    void testIdentity() {
        String name = "John";
        String surname = "Doe";
        
        identity.setName(name);
        identity.setSurname(surname);
        
        assertEquals(name, identity.getName());
        assertEquals(surname, identity.getSurname());
    }

    @Test
    void testEmptyValues() {
        identity.setName("");
        identity.setSurname("");
        
        assertEquals("", identity.getName());
        assertEquals("", identity.getSurname());
    }

    @Test
    void testNullValues() {
        identity.setName(null);
        identity.setSurname(null);
        
        assertNull(identity.getName());
        assertNull(identity.getSurname());
    }

    @Test
    void testSpecialCharacters() {
        String nameWithSpecialChars = "José-María";
        String surnameWithSpecialChars = "O'Connor";
        
        identity.setName(nameWithSpecialChars);
        identity.setSurname(surnameWithSpecialChars);
        
        assertEquals(nameWithSpecialChars, identity.getName());
        assertEquals(surnameWithSpecialChars, identity.getSurname());
    }

    @Test
    void testLongNames() {
        String longName = "A".repeat(1000);
        String longSurname = "B".repeat(1000);
        
        identity.setName(longName);
        identity.setSurname(longSurname);
        
        assertEquals(longName, identity.getName());
        assertEquals(longSurname, identity.getSurname());
    }
} 