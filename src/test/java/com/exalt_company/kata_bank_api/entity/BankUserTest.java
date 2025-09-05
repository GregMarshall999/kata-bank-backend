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
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void testDefaultConstructor() {
        assertNotNull(bankUser);
        assertEquals(0L, bankUser.getId());
        assertEquals(0, bankUser.getVersion());
        assertNull(bankUser.getIdentity());
        assertNull(bankUser.getCredentials());
        assertNull(bankUser.getBankRole());
        assertNull(bankUser.getAdvisor());
    }

    @Test
    void testInheritedFields() {
        long id = 123L;
        int version = 5;
        
        bankUser.setId(id);
        bankUser.setVersion(version);
        
        assertEquals(id, bankUser.getId());
        assertEquals(version, bankUser.getVersion());
    }

    @Test
    void testNullValues() {
        bankUser.setIdentity(null);
        bankUser.setCredentials(null);
        bankUser.setBankRole(null);
        bankUser.setAdvisor(null);
        
        assertNull(bankUser.getIdentity());
        assertNull(bankUser.getCredentials());
        assertNull(bankUser.getBankRole());
        assertNull(bankUser.getAdvisor());
    }

    @Test
    void testAllFieldsTogether() {
        long id = 1L;
        int version = 2;
        Identity testIdentity = new Identity();
        testIdentity.setName("Jane");
        testIdentity.setSurname("Smith");
        Credentials testCredentials = new Credentials();
        testCredentials.setEmail("jane.smith@example.com");
        testCredentials.setPassword("password123");
        BankRole role = BankRole.ADVISOR;
        BankUser advisor = new BankUser();
        advisor.setId(100L);
        
        bankUser.setId(id);
        bankUser.setVersion(version);
        bankUser.setIdentity(testIdentity);
        bankUser.setCredentials(testCredentials);
        bankUser.setBankRole(role);
        bankUser.setAdvisor(advisor);
        
        assertEquals(id, bankUser.getId());
        assertEquals(version, bankUser.getVersion());
        assertEquals(testIdentity, bankUser.getIdentity());
        assertEquals(testCredentials, bankUser.getCredentials());
        assertEquals(role, bankUser.getBankRole());
        assertEquals(advisor, bankUser.getAdvisor());
    }

    @Test
    void testUserDetailsWithNullCredentials() {
        bankUser.setCredentials(null);
        bankUser.setBankRole(BankRole.CLIENT);
        
        assertNull(bankUser.getUsername());
        assertNull(bankUser.getPassword());
        assertTrue(bankUser.getAuthorities().contains(new SimpleGrantedAuthority("CLIENT")));
    }

    @Test
    void testUserDetailsWithNullBankRole() {
        credentials.setEmail("test@example.com");
        credentials.setPassword("password123");
        bankUser.setCredentials(credentials);
        bankUser.setBankRole(null);
        
        assertEquals("test@example.com", bankUser.getUsername());
        assertEquals("password123", bankUser.getPassword());
        assertNotNull(bankUser.getAuthorities());
    }

    @Test
    void testUserDetailsWithEmptyCredentials() {
        credentials.setEmail("");
        credentials.setPassword("");
        bankUser.setCredentials(credentials);
        bankUser.setBankRole(BankRole.CLIENT);
        
        assertEquals("", bankUser.getUsername());
        assertEquals("", bankUser.getPassword());
        assertTrue(bankUser.getAuthorities().contains(new SimpleGrantedAuthority("CLIENT")));
    }

    @Test
    void testUserDetailsWithSpecialCharacters() {
        credentials.setEmail("test+tag@example.com");
        credentials.setPassword("P@ssw0rd!@#$%^&*()");
        bankUser.setCredentials(credentials);
        bankUser.setBankRole(BankRole.ADMIN);
        
        assertEquals("test+tag@example.com", bankUser.getUsername());
        assertEquals("P@ssw0rd!@#$%^&*()", bankUser.getPassword());
        assertTrue(bankUser.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")));
    }

    @Test
    void testUserDetailsWithUnicodeCharacters() {
        credentials.setEmail("tëst@exämple.com");
        credentials.setPassword("pässwörd123");
        bankUser.setCredentials(credentials);
        bankUser.setBankRole(BankRole.ADVISOR);
        
        assertEquals("tëst@exämple.com", bankUser.getUsername());
        assertEquals("pässwörd123", bankUser.getPassword());
        assertTrue(bankUser.getAuthorities().contains(new SimpleGrantedAuthority("ADVISOR")));
    }

    @Test
    void testUserDetailsWithLongCredentials() {
        String longEmail = "a".repeat(100) + "@example.com";
        String longPassword = "a".repeat(1000);
        credentials.setEmail(longEmail);
        credentials.setPassword(longPassword);
        bankUser.setCredentials(credentials);
        bankUser.setBankRole(BankRole.CLIENT);
        
        assertEquals(longEmail, bankUser.getUsername());
        assertEquals(longPassword, bankUser.getPassword());
        assertTrue(bankUser.getAuthorities().contains(new SimpleGrantedAuthority("CLIENT")));
    }

    @Test
    void testAdvisorRelationshipWithNull() {
        bankUser.setAdvisor(null);
        
        assertNull(bankUser.getAdvisor());
    }

    @Test
    void testAdvisorRelationshipWithSelf() {
        bankUser.setId(1L);
        bankUser.setAdvisor(bankUser);
        
        assertEquals(bankUser, bankUser.getAdvisor());
        assertEquals(1L, bankUser.getAdvisor().getId());
    }

    @Test
    void testAdvisorRelationshipWithDifferentUser() {
        BankUser advisor1 = new BankUser();
        advisor1.setId(10L);
        BankUser advisor2 = new BankUser();
        advisor2.setId(20L);
        
        bankUser.setAdvisor(advisor1);
        assertEquals(advisor1, bankUser.getAdvisor());
        assertEquals(10L, bankUser.getAdvisor().getId());
        
        bankUser.setAdvisor(advisor2);
        assertEquals(advisor2, bankUser.getAdvisor());
        assertEquals(20L, bankUser.getAdvisor().getId());
    }

    @Test
    void testIdentityAndCredentialsIndependence() {
        Identity identity1 = new Identity();
        identity1.setName("John");
        identity1.setSurname("Doe");
        Credentials credentials1 = new Credentials();
        credentials1.setEmail("john@example.com");
        credentials1.setPassword("password1");
        
        bankUser.setIdentity(identity1);
        bankUser.setCredentials(credentials1);
        
        assertEquals(identity1, bankUser.getIdentity());
        assertEquals(credentials1, bankUser.getCredentials());
        
        Identity identity2 = new Identity();
        identity2.setName("Jane");
        identity2.setSurname("Smith");
        bankUser.setIdentity(identity2);
        
        assertEquals(identity2, bankUser.getIdentity());
        assertEquals(credentials1, bankUser.getCredentials());
    }

    @Test
    void testBankRoleIndependence() {
        bankUser.setBankRole(BankRole.CLIENT);
        assertEquals(BankRole.CLIENT, bankUser.getBankRole());
        
        bankUser.setBankRole(BankRole.ADVISOR);
        assertEquals(BankRole.ADVISOR, bankUser.getBankRole());
        
        bankUser.setBankRole(BankRole.ADMIN);
        assertEquals(BankRole.ADMIN, bankUser.getBankRole());
    }

    @Test
    void testUserDetailsDefaultMethodsConsistency() {
        assertTrue(bankUser.isAccountNonExpired());
        assertTrue(bankUser.isAccountNonLocked());
        assertTrue(bankUser.isCredentialsNonExpired());
        assertTrue(bankUser.isEnabled());
        
        assertTrue(bankUser.isAccountNonExpired());
        assertTrue(bankUser.isAccountNonLocked());
        assertTrue(bankUser.isCredentialsNonExpired());
        assertTrue(bankUser.isEnabled());
    }

    @Test
    void testRealisticBankingScenario() {
        long id = 1001L;
        int version = 3;
        Identity testIdentity = new Identity();
        testIdentity.setName("Alice");
        testIdentity.setSurname("Johnson");
        Credentials testCredentials = new Credentials();
        testCredentials.setEmail("alice.johnson@bank.com");
        testCredentials.setPassword("SecurePass123!");
        BankRole role = BankRole.ADVISOR;
        BankUser advisor = new BankUser();
        advisor.setId(5001L);
        
        bankUser.setId(id);
        bankUser.setVersion(version);
        bankUser.setIdentity(testIdentity);
        bankUser.setCredentials(testCredentials);
        bankUser.setBankRole(role);
        bankUser.setAdvisor(advisor);
        
        assertEquals(id, bankUser.getId());
        assertEquals(version, bankUser.getVersion());
        assertEquals(testIdentity, bankUser.getIdentity());
        assertEquals(testCredentials, bankUser.getCredentials());
        assertEquals(role, bankUser.getBankRole());
        assertEquals(advisor, bankUser.getAdvisor());
        
        assertEquals("alice.johnson@bank.com", bankUser.getUsername());
        assertEquals("SecurePass123!", bankUser.getPassword());
        assertTrue(bankUser.getAuthorities().contains(new SimpleGrantedAuthority("ADVISOR")));
        assertTrue(bankUser.isAccountNonExpired());
        assertTrue(bankUser.isAccountNonLocked());
        assertTrue(bankUser.isCredentialsNonExpired());
        assertTrue(bankUser.isEnabled());
    }
} 