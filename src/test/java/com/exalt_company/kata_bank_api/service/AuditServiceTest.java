package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.statement.AccountStatementDto;
import com.exalt_company.kata_bank_api.dto.statement.OperationDto;
import com.exalt_company.kata_bank_api.entity.AccountAudit;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.AccountType;
import com.exalt_company.kata_bank_api.enums.AuditOperation;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.exception.AuditException;
import com.exalt_company.kata_bank_api.repository.AccountAuditRepository;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.repository.SavingRepository;
import com.exalt_company.kata_bank_api.service.impl.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {
    @Mock
    private AccountAuditRepository auditRepository;

    @Mock
    private BankUserRepository bankUserRepository;

    @Mock
    private FundRepository fundRepository;

    @Mock
    private SavingRepository savingRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuditService auditService;

    private BankUser testUser;
    private BankUser adminUser;
    private BankUser unauthorizedUser;
    private Fund testFund;
    private Saving testSaving;
    private AccountAudit testAudit;

    @BeforeEach
    void setUp() {
        testUser = new BankUser();
        testUser.setId(1L);
        testUser.setBankRole(BankRole.CLIENT);

        Identity testIdentity = new Identity();
        testIdentity.setSurname("Doe");
        testIdentity.setName("John");
        testUser.setIdentity(testIdentity);

        adminUser = new BankUser();
        adminUser.setId(2L);
        adminUser.setBankRole(BankRole.ADMIN);

        Identity adminIdentity = new Identity();
        adminIdentity.setSurname("Admin");
        adminIdentity.setName("Admin");
        adminUser.setIdentity(adminIdentity);

        unauthorizedUser = new BankUser();
        unauthorizedUser.setId(3L);
        unauthorizedUser.setBankRole(BankRole.CLIENT);

        Identity unauthorizedIdentity = new Identity();
        unauthorizedIdentity.setSurname("Unauthorized");
        unauthorizedIdentity.setName("User");
        unauthorizedUser.setIdentity(unauthorizedIdentity);

        testFund = new Fund();
        testFund.setId(1L);
        testFund.setBalance(1000.0);
        testFund.setOwner(testUser);

        testSaving = new Saving();
        testSaving.setId(1L);
        testSaving.setBalance(500.0);
        testSaving.setOwner(testUser);

        testAudit = new AccountAudit();
        testAudit.setId(1L);
        testAudit.setOperation(AuditOperation.DEPOSIT);
        testAudit.setAmount(100.0);
        testAudit.setBalanceBefore(900.0);
        testAudit.setBalanceAfter(1000.0);
        testAudit.setRequestingUser(testUser);
        testAudit.setUserFund(testFund);
        testAudit.setUserSaving(null);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testRecordAudit_Success() {
        when(auditRepository.save(any(AccountAudit.class))).thenReturn(testAudit);

        auditService.recordAudit(
            AuditOperation.DEPOSIT, 
            100.0, 
            900.0, 
            1000.0, 
            testUser, 
            testFund, 
            null
        );

        verify(auditRepository).save(any(AccountAudit.class));
    }

    @Test
    void testRecordAudit_WithSaving() {
        when(auditRepository.save(any(AccountAudit.class))).thenReturn(testAudit);

        auditService.recordAudit(
            AuditOperation.WITHDRAW, 
            50.0, 
            500.0, 
            450.0, 
            testUser, 
            null, 
            testSaving
        );

        verify(auditRepository).save(any(AccountAudit.class));
    }

    @Test
    void testRequestStatement_FundAccount_Success() throws AuditException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        Page<AccountAudit> auditPage = new PageImpl<>(List.of(testAudit), PageRequest.of(0, 10), 1);
        
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fundRepository.findByOwner(testUser)).thenReturn(Optional.of(testFund));
        when(auditRepository.findByUserFundOwnerCurrentMonth(eq(testUser), any(Pageable.class)))
            .thenReturn(auditPage);

        ResponseEntity<AccountStatementDto> response = auditService.requestStatement("FUND", 1L, 0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        AccountStatementDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals(AccountType.FUND, dto.getAccountType());
        assertEquals(1000.0, dto.getAccountBalance());
        assertEquals(1, dto.getOperations().size());
        assertEquals(0, dto.getOperationsPage());
        assertEquals(10, dto.getOperationsSize());
        assertEquals(1, dto.getTotalOperationsPage());

        OperationDto operation = dto.getOperations().get(0);
        assertEquals(AuditOperation.DEPOSIT, operation.getOperation());
        assertEquals("John Doe", operation.getOperationAuthor());

        verify(bankUserRepository).findById(1L);
        verify(fundRepository).findByOwner(testUser);
        verify(auditRepository).findByUserFundOwnerCurrentMonth(eq(testUser), any(Pageable.class));
    }

    @Test
    void testRequestStatement_SavingAccount_Success() throws AuditException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        Page<AccountAudit> auditPage = new PageImpl<>(List.of(testAudit), PageRequest.of(0, 10), 1);
        
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(savingRepository.findByOwner(testUser)).thenReturn(Optional.of(testSaving));
        when(auditRepository.findByUserSavingOwnerCurrentMonth(eq(testUser), any(Pageable.class)))
            .thenReturn(auditPage);

        ResponseEntity<AccountStatementDto> response = auditService.requestStatement("SAVING", 1L, 0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        AccountStatementDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals(AccountType.SAVING, dto.getAccountType());
        assertEquals(500.0, dto.getAccountBalance());
        assertEquals(1, dto.getOperations().size());
        assertEquals(0, dto.getOperationsPage());
        assertEquals(10, dto.getOperationsSize());
        assertEquals(1, dto.getTotalOperationsPage());

        OperationDto operation = dto.getOperations().get(0);
        assertEquals(AuditOperation.DEPOSIT, operation.getOperation());
        assertEquals("John Doe", operation.getOperationAuthor());

        verify(bankUserRepository).findById(1L);
        verify(savingRepository).findByOwner(testUser);
        verify(auditRepository).findByUserSavingOwnerCurrentMonth(eq(testUser), any(Pageable.class));
    }

    @Test
    void testRequestStatement_UserNotFound_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(999L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        when(bankUserRepository.findById(999L)).thenReturn(Optional.empty());

        AuditException exception = assertThrows(AuditException.class,
            () -> auditService.requestStatement("FUND", 999L, 0, 10));
        
        assertEquals("Can't find funds owner", exception.getMessage());
        verify(bankUserRepository).findById(999L);
        verify(fundRepository, never()).findByOwner(any());
        verify(auditRepository, never()).findByUserFundOwnerCurrentMonth(any(), any());
    }

    @Test
    void testRequestStatement_FundNotFound_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fundRepository.findByOwner(testUser)).thenReturn(Optional.empty());

        AuditException exception = assertThrows(AuditException.class,
            () -> auditService.requestStatement("FUND", 1L, 0, 10));
        
        assertEquals("Could not find user's funds", exception.getMessage());
        verify(bankUserRepository).findById(1L);
        verify(fundRepository).findByOwner(testUser);
        verify(auditRepository, never()).findByUserFundOwnerCurrentMonth(any(), any());
    }

    @Test
    void testRequestStatement_SavingNotFound_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(savingRepository.findByOwner(testUser)).thenReturn(Optional.empty());

        AuditException exception = assertThrows(AuditException.class,
            () -> auditService.requestStatement("SAVING", 1L, 0, 10));
        
        assertEquals("Could not find user's Savings", exception.getMessage());
        verify(bankUserRepository).findById(1L);
        verify(savingRepository).findByOwner(testUser);
        verify(auditRepository, never()).findByUserSavingOwnerCurrentMonth(any(), any());
    }

    @Test
    void testRequestStatement_InvalidAccountType_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        AuditException exception = assertThrows(AuditException.class,
            () -> auditService.requestStatement("INVALID", 1L, 0, 10));
        
        assertEquals("Wrong account type", exception.getMessage());
        verify(bankUserRepository, never()).findById(any());
        verify(fundRepository, never()).findByOwner(any());
        verify(auditRepository, never()).findByUserFundOwnerCurrentMonth(any(), any());
    }

    @Test
    void testRequestStatement_InvalidAccountTypeValue_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        AuditException exception = assertThrows(AuditException.class,
            () -> auditService.requestStatement("INVALID_TYPE", 1L, 0, 10));
        
        assertEquals("Wrong account type", exception.getMessage());
        verify(bankUserRepository, never()).findById(any());
        verify(fundRepository, never()).findByOwner(any());
        verify(auditRepository, never()).findByUserFundOwnerCurrentMonth(any(), any());
    }

    @Test
    void testRequestStatement_EmptyAuditPage() throws AuditException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        Page<AccountAudit> emptyAuditPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fundRepository.findByOwner(testUser)).thenReturn(Optional.of(testFund));
        when(auditRepository.findByUserFundOwnerCurrentMonth(eq(testUser), any(Pageable.class)))
            .thenReturn(emptyAuditPage);

        ResponseEntity<AccountStatementDto> response = auditService.requestStatement("FUND", 1L, 0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        AccountStatementDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals(AccountType.FUND, dto.getAccountType());
        assertEquals(1000.0, dto.getAccountBalance());
        assertEquals(0, dto.getOperations().size());
        assertEquals(0, dto.getOperationsPage());
        assertEquals(10, dto.getOperationsSize());
        assertEquals(0, dto.getTotalOperationsPage());
    }

    @Test
    void testRequestStatement_EmptyAuditPage_Saving() throws AuditException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        Page<AccountAudit> emptyAuditPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(savingRepository.findByOwner(testUser)).thenReturn(Optional.of(testSaving));
        when(auditRepository.findByUserSavingOwnerCurrentMonth(eq(testUser), any(Pageable.class)))
            .thenReturn(emptyAuditPage);

        ResponseEntity<AccountStatementDto> response = auditService.requestStatement("SAVING", 1L, 0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        AccountStatementDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals(AccountType.SAVING, dto.getAccountType());
        assertEquals(500.0, dto.getAccountBalance());
        assertEquals(0, dto.getOperations().size());
        assertEquals(0, dto.getOperationsPage());
        assertEquals(10, dto.getOperationsSize());
        assertEquals(0, dto.getTotalOperationsPage());
    }

    @Test
    void testRequestStatement_Pagination() throws AuditException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        Page<AccountAudit> auditPage = new PageImpl<>(List.of(testAudit), PageRequest.of(1, 5), 10);
        
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fundRepository.findByOwner(testUser)).thenReturn(Optional.of(testFund));
        when(auditRepository.findByUserFundOwnerCurrentMonth(eq(testUser), any(Pageable.class)))
            .thenReturn(auditPage);

        ResponseEntity<AccountStatementDto> response = auditService.requestStatement("FUND", 1L, 1, 5);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        AccountStatementDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals(1, dto.getOperationsPage());
        assertEquals(5, dto.getOperationsSize());
        assertEquals(2, dto.getTotalOperationsPage());
    }

    @Test
    void testRequestStatement_Pagination_Saving() throws AuditException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        Page<AccountAudit> auditPage = new PageImpl<>(List.of(testAudit), PageRequest.of(1, 5), 10);
        
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(savingRepository.findByOwner(testUser)).thenReturn(Optional.of(testSaving));
        when(auditRepository.findByUserSavingOwnerCurrentMonth(eq(testUser), any(Pageable.class)))
            .thenReturn(auditPage);

        ResponseEntity<AccountStatementDto> response = auditService.requestStatement("SAVING", 1L, 1, 5);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        AccountStatementDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals(1, dto.getOperationsPage());
        assertEquals(5, dto.getOperationsSize());
        assertEquals(2, dto.getTotalOperationsPage());
    }

    @Test
    void testRequestStatement_AuthorNameFormat() throws AuditException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        Page<AccountAudit> auditPage = new PageImpl<>(List.of(testAudit), PageRequest.of(0, 10), 1);
        
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fundRepository.findByOwner(testUser)).thenReturn(Optional.of(testFund));
        when(auditRepository.findByUserFundOwnerCurrentMonth(eq(testUser), any(Pageable.class)))
            .thenReturn(auditPage);

        ResponseEntity<AccountStatementDto> response = auditService.requestStatement("FUND", 1L, 0, 10);

        AccountStatementDto dto = response.getBody();
        OperationDto operation = dto.getOperations().get(0);
        
        assertEquals("John Doe", operation.getOperationAuthor());
    }

    @Test
    void testRequestStatement_AdminUser_CanAccessAnyAccount() throws AuditException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ADMIN"))).when(authentication).getAuthorities();

        Page<AccountAudit> auditPage = new PageImpl<>(List.of(testAudit), PageRequest.of(0, 10), 1);
        
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fundRepository.findByOwner(testUser)).thenReturn(Optional.of(testFund));
        when(auditRepository.findByUserFundOwnerCurrentMonth(eq(testUser), any(Pageable.class)))
            .thenReturn(auditPage);

        ResponseEntity<AccountStatementDto> response = auditService.requestStatement("FUND", 1L, 0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        AccountStatementDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals(AccountType.FUND, dto.getAccountType());

        verify(bankUserRepository).findById(1L);
        verify(fundRepository).findByOwner(testUser);
        verify(auditRepository).findByUserFundOwnerCurrentMonth(eq(testUser), any(Pageable.class));
    }

    @Test
    void testRequestStatement_UnauthorizedUser_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(3L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        AuditException exception = assertThrows(AuditException.class,
            () -> auditService.requestStatement("FUND", 1L, 0, 10));
        
        assertEquals("Attempted to access unauthorized statement", exception.getMessage());
        verify(bankUserRepository, never()).findById(any());
        verify(fundRepository, never()).findByOwner(any());
        verify(auditRepository, never()).findByUserFundOwnerCurrentMonth(any(), any());
    }

    @Test
    void testRequestStatement_NullCredentials_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(null);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        AuditException exception = assertThrows(AuditException.class,
            () -> auditService.requestStatement("FUND", 1L, 0, 10));
        
        assertEquals("An error has occured with the user credentials", exception.getMessage());
        verify(bankUserRepository, never()).findById(any());
        verify(fundRepository, never()).findByOwner(any());
        verify(auditRepository, never()).findByUserFundOwnerCurrentMonth(any(), any());
    }

    @Test
    void testRequestStatement_InvalidCredentials_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn("invalid_credentials");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        AuditException exception = assertThrows(AuditException.class,
            () -> auditService.requestStatement("FUND", 1L, 0, 10));
        
        assertEquals("An error has occured with the user credentials", exception.getMessage());
        verify(bankUserRepository, never()).findById(any());
        verify(fundRepository, never()).findByOwner(any());
        verify(auditRepository, never()).findByUserFundOwnerCurrentMonth(any(), any());
    }

    @Test
    void testRequestStatement_ZeroUserId_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(0L);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("CLIENT"))).when(authentication).getAuthorities();

        AuditException exception = assertThrows(AuditException.class,
            () -> auditService.requestStatement("FUND", 1L, 0, 10));
        
        assertEquals("Statement access unauthorized", exception.getMessage());
        verify(bankUserRepository, never()).findById(any());
        verify(fundRepository, never()).findByOwner(any());
        verify(auditRepository, never()).findByUserFundOwnerCurrentMonth(any(), any());
    }

    @Test
    void testRequestStatement_AdminCanAccessDifferentUserAccount() throws AuditException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ADMIN"))).when(authentication).getAuthorities();

        Page<AccountAudit> auditPage = new PageImpl<>(List.of(testAudit), PageRequest.of(0, 10), 1);
        
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(savingRepository.findByOwner(testUser)).thenReturn(Optional.of(testSaving));
        when(auditRepository.findByUserSavingOwnerCurrentMonth(eq(testUser), any(Pageable.class)))
            .thenReturn(auditPage);

        ResponseEntity<AccountStatementDto> response = auditService.requestStatement("SAVING", 1L, 0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        AccountStatementDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals(AccountType.SAVING, dto.getAccountType());

        verify(bankUserRepository).findById(1L);
        verify(savingRepository).findByOwner(testUser);
        verify(auditRepository).findByUserSavingOwnerCurrentMonth(eq(testUser), any(Pageable.class));
    }
}
