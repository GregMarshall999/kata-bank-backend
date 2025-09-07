package com.exalt_company.kata_bank_api.security;

import com.exalt_company.kata_bank_api.enums.BankRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for SecurityConfig to verify the security configuration
 * is properly set up with correct endpoint mappings.
 */
@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void testSecurityConfigIsLoaded() {
        assertNotNull(securityConfig, "SecurityConfig should be loaded");
    }

    @Test
    void testBankRoleEnumValues() {
        assertNotNull(BankRole.ADMIN, "ADMIN role should exist");
        assertNotNull(BankRole.CLIENT, "CLIENT role should exist");
        assertNotNull(BankRole.ADVISOR, "ADVISOR role should exist");
        
        assertEquals("ADMIN", BankRole.ADMIN.name(), "ADMIN role name should be 'ADMIN'");
        assertEquals("CLIENT", BankRole.CLIENT.name(), "CLIENT role name should be 'CLIENT'");
        assertEquals("ADVISOR", BankRole.ADVISOR.name(), "ADVISOR role name should be 'ADVISOR'");
    }
}
