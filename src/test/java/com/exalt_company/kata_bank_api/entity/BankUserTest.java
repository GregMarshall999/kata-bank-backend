package com.exalt_company.kata_bank_api.entity;

import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankUserTest {
    private BankUser bankUser;
    private Identity identity;
    private Credentials credentials;

    @BeforeEach
    void setUp() {
        bankUser = new BankUser();
        identity = new Identity();
        credentials = new Credentials();
    }

    @Test
    void testBankUserCreation() {
        identity.setName("John");
        identity.setSurname("Doe");
        credentials.setEmail("john.doe@example.com");
        credentials.setPassword("password123");
        
        bankUser.setIdentity(identity);
        bankUser.setCredentials(credentials);
        bankUser.setBankRole(BankRole.CLIENT);
        
        assertNotNull(bankUser.getIdentity());
        assertNotNull(bankUser.getCredentials());
        assertEquals(BankRole.CLIENT, bankUser.getBankRole());
        assertEquals("John", bankUser.getIdentity().getName());
        assertEquals("Doe", bankUser.getIdentity().getSurname());
        assertEquals("john.doe@example.com", bankUser.getCredentials().getEmail());
        assertEquals("password123", bankUser.getCredentials().getPassword());
    }

    @Test
    void testUserDetailsImplementation() {
        credentials.setEmail("test@example.com");
        credentials.setPassword("encodedPassword");
        bankUser.setCredentials(credentials);
        bankUser.setBankRole(BankRole.ADMIN);
        
        String username = bankUser.getUsername();
        String password = bankUser.getPassword();
        Collection<?> authorities = bankUser.getAuthorities();
        
        assertEquals("test@example.com", username);
        assertEquals("encodedPassword", password);
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ADMIN")));
        assertEquals(1, authorities.size());
    }

    @Test
    void testUserDetailsDefaultMethods() {
        assertTrue(bankUser.isAccountNonExpired());
        assertTrue(bankUser.isAccountNonLocked());
        assertTrue(bankUser.isCredentialsNonExpired());
        assertTrue(bankUser.isEnabled());
    }

    @Test
    void testAdvisorRelationship() {
        BankUser advisor = new BankUser();
        advisor.setId(1L);
        
        bankUser.setAdvisor(advisor);
        
        assertNotNull(bankUser.getAdvisor());
        assertEquals(1L, bankUser.getAdvisor().getId());
    }

    @Test
    void testDifferentBankRoles() {
        bankUser.setBankRole(BankRole.CLIENT);
        Collection<?> clientAuthorities = bankUser.getAuthorities();
        assertTrue(clientAuthorities.contains(new SimpleGrantedAuthority("CLIENT")));
        
        bankUser.setBankRole(BankRole.ADVISOR);
        Collection<?> advisorAuthorities = bankUser.getAuthorities();
        assertTrue(advisorAuthorities.contains(new SimpleGrantedAuthority("ADVISOR")));
        
        bankUser.setBankRole(BankRole.ADMIN);
        Collection<?> adminAuthorities = bankUser.getAuthorities();
        assertTrue(adminAuthorities.contains(new SimpleGrantedAuthority("ADMIN")));
    }

    @Test
    void testSettersAndGetters() {
        Identity newIdentity = new Identity();
        Credentials newCredentials = new Credentials();
        BankUser newAdvisor = new BankUser();
        
        bankUser.setIdentity(newIdentity);
        bankUser.setCredentials(newCredentials);
        bankUser.setBankRole(BankRole.ADVISOR);
        bankUser.setAdvisor(newAdvisor);
        
        assertEquals(newIdentity, bankUser.getIdentity());
        assertEquals(newCredentials, bankUser.getCredentials());
        assertEquals(BankRole.ADVISOR, bankUser.getBankRole());
        assertEquals(newAdvisor, bankUser.getAdvisor());
    }
} 