package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.enums.AuditOperation;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.SavingException;
import com.exalt_company.kata_bank_api.mapper.SavingMapper;
import com.exalt_company.kata_bank_api.repository.AccountAuditRepository;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.repository.SavingRepository;
import com.exalt_company.kata_bank_api.service.impl.SavingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingServiceTest {
    @Mock
    private SavingRepository repository;

    @Mock
    private SavingMapper mapper;

    @Mock
    private BankUserRepository bankUserRepository;

    @Mock
    private AccountAuditRepository accountAuditRepository;

    @Mock
    private IAuditService auditService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SavingService savingService;

    private Saving testSaving;
    private SavingDto savingDto;

    @BeforeEach
    void setUp() {
        BankUser testUser = new BankUser();
        testUser.setId(1L);
        Credentials credentials = new Credentials();
        credentials.setEmail("test@example.com");
        testUser.setCredentials(credentials);
        testUser.setBankRole(BankRole.CLIENT);

        testSaving = new Saving();
        testSaving.setId(1L);
        testSaving.setBalance(1000.0);
        testSaving.setMaxBalance(5000.0);
        testSaving.setOwner(testUser);

        savingDto = new SavingDto();
        savingDto.setId(1L);
        savingDto.setBalance(100.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(1L);

        // Setup SecurityContext mock
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testOpenSavingsAccountSuccess() throws SavingException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testSaving.getOwner()));
        when(repository.findByOwner(testSaving.getOwner())).thenReturn(Optional.empty());
        when(mapper.toEntity(savingDto)).thenReturn(testSaving);
        when(repository.save(any(Saving.class))).thenReturn(testSaving);

        ResponseEntity<Banking> response = savingService.openSavingsAccount(savingDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(Banking.AUTHORIZED, response.getBody());

        verify(repository).findById(1L);
        verify(bankUserRepository).findById(1L);
        verify(repository).findByOwner(testSaving.getOwner());
        verify(auditService).recordAudit(any(AuditOperation.class), anyDouble(), anyDouble(), anyDouble(), any(BankUser.class), any(), any(Saving.class));
        verify(repository).save(any(Saving.class));
    }

    @Test
    void testOpenSavingsAccountWithNegativeMaxBalance() {
        savingDto.setMaxBalance(-100.0);

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.openSavingsAccount(savingDto)
        );

        assertEquals("Wrong value for max balance", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testOpenSavingsAccountWithExistingSaving() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testSaving));

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.openSavingsAccount(savingDto)
        );

        assertEquals("Can't open same savings twice", exception.getMessage());
        verify(repository).findById(1L);
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testOpenSavingsAccountUnauthorizedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(2L);

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.openSavingsAccount(savingDto)
        );

        assertEquals("Attempted to access unauthorized savings", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testOpenSavingsAccountUserAlreadyHasSaving() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testSaving.getOwner()));
        when(repository.findByOwner(testSaving.getOwner())).thenReturn(Optional.of(testSaving));

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.openSavingsAccount(savingDto)
        );

        assertEquals("User can only have one savings account", exception.getMessage());
        verify(repository).findById(1L);
        verify(bankUserRepository).findById(1L);
        verify(repository).findByOwner(testSaving.getOwner());
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testCloseSavingsAccountSuccess() throws SavingException {
        testSaving.setBalance(0.0);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testSaving));
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testSaving.getOwner()));
        when(accountAuditRepository.findAllByUserSaving(testSaving)).thenReturn(new ArrayList<>());

        ResponseEntity<Banking> response = savingService.closeSavingsAccount(savingDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.COMPLETED, response.getBody());

        verify(repository).findById(1L);
        verify(bankUserRepository).findById(1L);
        verify(accountAuditRepository).findAllByUserSaving(testSaving);
        verify(auditService).recordAudit(any(AuditOperation.class), anyDouble(), anyDouble(), anyDouble(), any(BankUser.class), any(), any());
        verify(repository).delete(any(Saving.class));
    }

    @Test
    void testCloseSavingsAccountWithNonZeroBalance() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testSaving));

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.closeSavingsAccount(savingDto)
        );

        assertEquals("Can not close savings if balance not empty", exception.getMessage());
        verify(repository).findById(1L);
        verify(repository, never()).delete(any(Saving.class));
    }

    @Test
    void testCloseSavingsAccountWithNonExistentSaving() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.closeSavingsAccount(savingDto)
        );

        assertEquals("Could not close non existing savings", exception.getMessage());
        verify(repository).findById(1L);
        verify(repository, never()).delete(any(Saving.class));
    }

    @Test
    void testCloseSavingsAccountUnauthorizedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(2L);

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.closeSavingsAccount(savingDto)
        );

        assertEquals("Attempted to access unauthorized savings", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).delete(any(Saving.class));
    }

    @Test
    void testDepositSuccess() throws SavingException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testSaving));
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testSaving.getOwner()));
        when(repository.save(any(Saving.class))).thenReturn(testSaving);

        ResponseEntity<Banking> response = savingService.deposit(savingDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.DEPOSITED, response.getBody());

        verify(repository).findById(1L);
        verify(bankUserRepository).findById(1L);
        verify(auditService).recordAudit(any(AuditOperation.class), anyDouble(), anyDouble(), anyDouble(), any(BankUser.class), any(), any(Saving.class));
        verify(repository).save(argThat(saving ->
            saving.getBalance() == 1100.0
        ));
    }

    @Test
    void testDepositWithZeroId() {
        savingDto.setId(0L);

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.deposit(savingDto)
        );

        assertEquals("Please open a savings account before depositing here", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testDepositWithNegativeAmount() {
        savingDto.setBalance(-100.0);

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.deposit(savingDto)
        );

        assertEquals("Wrong value for balance", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testDepositExceedingMaxBalance() {
        savingDto.setBalance(5000.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testSaving));

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.deposit(savingDto)
        );

        assertEquals("Savings cannot exceed the maximum allowed balance", exception.getMessage());
        verify(repository).findById(1L);
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testDepositWithNonExistentSaving() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.deposit(savingDto)
        );

        assertEquals("No savings to deposit", exception.getMessage());
        verify(repository).findById(1L);
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testDepositUnauthorizedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(2L);

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.deposit(savingDto)
        );

        assertEquals("Attempted to access unauthorized savings", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testWithdrawSuccess() throws SavingException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testSaving));
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testSaving.getOwner()));
        when(repository.save(any(Saving.class))).thenReturn(testSaving);

        ResponseEntity<Banking> response = savingService.withdraw(savingDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.WITHDREW, response.getBody());

        verify(repository).findById(1L);
        verify(bankUserRepository).findById(1L);
        verify(auditService).recordAudit(any(AuditOperation.class), anyDouble(), anyDouble(), anyDouble(), any(BankUser.class), any(), any(Saving.class));
        verify(repository).save(argThat(saving -> 
            saving.getBalance() == 900.0
        ));
    }

    @Test
    void testWithdrawWithZeroId() {
        savingDto.setId(0L);

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.withdraw(savingDto)
        );

        assertEquals("No savings to withdraw from", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testWithdrawWithNegativeAmount() {
        savingDto.setBalance(-100.0);

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.withdraw(savingDto)
        );

        assertEquals("Wrong value for balance", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testWithdrawExceedingBalance() {
        savingDto.setBalance(1500.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testSaving));

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.withdraw(savingDto)
        );

        assertEquals("Attempting to withdraw more than allowed", exception.getMessage());
        verify(repository).findById(1L);
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testWithdrawWithNonExistentSaving() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.withdraw(savingDto)
        );

        assertEquals("No savings to withdraw from", exception.getMessage());
        verify(repository).findById(1L);
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testWithdrawUnauthorizedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(2L);

        SavingException exception = assertThrows(SavingException.class, () ->
            savingService.withdraw(savingDto)
        );

        assertEquals("Attempted to access unauthorized savings", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Saving.class));
    }

    @Test
    void testWithdrawExactBalance() throws SavingException {
        savingDto.setBalance(1000.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testSaving));
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testSaving.getOwner()));
        when(repository.save(any(Saving.class))).thenReturn(testSaving);

        ResponseEntity<Banking> response = savingService.withdraw(savingDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.WITHDREW, response.getBody());

        verify(repository).findById(1L);
        verify(bankUserRepository).findById(1L);
        verify(auditService).recordAudit(any(AuditOperation.class), anyDouble(), anyDouble(), anyDouble(), any(BankUser.class), any(), any(Saving.class));
        verify(repository).save(argThat(saving -> 
            saving.getBalance() == 0.0
        ));
    }

    @Test
    void testDepositAtMaxBalance() throws SavingException {
        testSaving.setBalance(4000.0);
        savingDto.setBalance(1000.0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testSaving));
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testSaving.getOwner()));
        when(repository.save(any(Saving.class))).thenReturn(testSaving);

        ResponseEntity<Banking> response = savingService.deposit(savingDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.DEPOSITED, response.getBody());

        verify(repository).findById(1L);
        verify(bankUserRepository).findById(1L);
        verify(auditService).recordAudit(any(AuditOperation.class), anyDouble(), anyDouble(), anyDouble(), any(BankUser.class), any(), any(Saving.class));
        verify(repository).save(argThat(saving -> 
            saving.getBalance() == 5000.0
        ));
    }

    @Test
    void testOpenSavingsAccountWithNullDto() {
        assertThrows(NullPointerException.class, () ->
            savingService.openSavingsAccount(null)
        );
    }

    @Test
    void testCloseSavingsAccountWithNullDto() {
        assertThrows(SavingException.class, () ->
            savingService.closeSavingsAccount(null)
        );
    }

    @Test
    void testDepositWithNullDto() {
        assertThrows(SavingException.class, () ->
            savingService.deposit(null)
        );
    }

    @Test
    void testWithdrawWithNullDto() {
        assertThrows(SavingException.class, () ->
            savingService.withdraw(null)
        );
    }
}
