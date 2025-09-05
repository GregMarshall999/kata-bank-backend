package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.enums.AuditOperation;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.mapper.FundMapper;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.service.impl.FundService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FundServiceTest {
    @Mock
    private FundRepository repository;

    @Mock
    private FundMapper mapper;

    @Mock
    private BankUserRepository bankUserRepository;

    @Mock
    private IAuditService auditService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FundService fundService;

    private Fund testFund;
    private BankUser testUser;
    private FundOpDto fundOpDto;
    private OverdrawDto overdrawDto;

    @BeforeEach
    void setUp() {
        testUser = new BankUser();
        testUser.setId(1L);
        Credentials credentials = new Credentials();
        credentials.setEmail("test@example.com");
        testUser.setCredentials(credentials);
        testUser.setBankRole(BankRole.CLIENT);

        testFund = new Fund();
        testFund.setId(1L);
        testFund.setBalance(1000.0);
        testFund.setOwner(testUser);
        testFund.setCanOverdraw(false);
        testFund.setMaxOverdraw(0.0);

        fundOpDto = new FundOpDto();
        fundOpDto.setId(1L);
        fundOpDto.setBalance(100.0);
        fundOpDto.setOwnerId(1L);

        overdrawDto = new OverdrawDto();
        overdrawDto.setId(1L);
        overdrawDto.setMaxOverdraw(500.0);
        overdrawDto.setOwnerId(1L);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testDeposit_NewFund_Success() throws FundException {
        fundOpDto.setId(0L);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(mapper.toEntity(fundOpDto)).thenReturn(testFund);
        when(repository.save(any(Fund.class))).thenReturn(testFund);
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.findByOwner(testUser)).thenReturn(Optional.empty());

        ResponseEntity<Banking> response = fundService.deposit(fundOpDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.DEPOSITED, response.getBody());

        verify(mapper).toEntity(fundOpDto);
        verify(repository).save(testFund);
        verify(bankUserRepository).findById(1L);
        verify(auditService).recordAudit(AuditOperation.DEPOSIT, 100.0, 0.0, 1000.0, testUser, testFund, null);
    }

    @Test
    void testDeposit_ExistingFund_Success() throws FundException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.findByOwner(testUser)).thenReturn(Optional.empty());

        ResponseEntity<Banking> response = fundService.deposit(fundOpDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.DEPOSITED, response.getBody());

        verify(repository).findById(1L);
        verify(repository).save(argThat(fund -> fund.getBalance() == 1100.0));
        verify(bankUserRepository).findById(1L);
        verify(auditService).recordAudit(AuditOperation.DEPOSIT, 100.0, 1000.0, 1100.0, testUser, testFund, null);
    }

    @Test
    void testDeposit_NullDto_ThrowsException() {
        FundException exception = assertThrows(FundException.class, () ->
            fundService.deposit(null)
        );
        assertEquals("Wrong request body", exception.getMessage());
    }

    @Test
    void testDeposit_NegativeAmount_ThrowsException() {
        fundOpDto.setBalance(-100.0);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.deposit(fundOpDto)
        );
        assertEquals("Wrong value for balance", exception.getMessage());
    }

    @Test
    void testDeposit_UserAlreadyHasFund_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.findByOwner(testUser)).thenReturn(Optional.of(testFund));

        FundException exception = assertThrows(FundException.class, () ->
            fundService.deposit(fundOpDto)
        );
        assertEquals("User can only have one funds account", exception.getMessage());
    }

    @Test
    void testDeposit_NonExistentUser_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(bankUserRepository.findById(1L)).thenReturn(Optional.empty());

        FundException exception = assertThrows(FundException.class, () ->
            fundService.deposit(fundOpDto)
        );
        assertEquals("Could not find funds owner", exception.getMessage());
    }

    @Test
    void testDeposit_NonExistentFund_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.findByOwner(testUser)).thenReturn(Optional.empty());

        FundException exception = assertThrows(FundException.class, () ->
            fundService.deposit(fundOpDto)
        );
        assertEquals("No balance to add funds", exception.getMessage());
    }

    @Test
    void testDeposit_UnauthorizedUser_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(2L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.deposit(fundOpDto)
        );
        assertEquals("Attempted to access unauthorized funds", exception.getMessage());
    }

    @Test
    void testWithdraw_ValidAmount_Success() throws FundException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<Banking> response = fundService.withdraw(fundOpDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.WITHDREW, response.getBody());

        verify(repository).findById(1L);
        verify(repository).save(argThat(fund -> fund.getBalance() == 900.0));
        verify(bankUserRepository).findById(1L);
        verify(auditService).recordAudit(AuditOperation.WITHDRAW, 100.0, 1000.0, 900.0, testUser, testFund, null);
    }

    @Test
    void testWithdraw_WithOverdrawEnabled_Success() throws FundException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(100.0);
        fundOpDto.setBalance(200.0);

        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<Banking> response = fundService.withdraw(fundOpDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.WITHDREW, response.getBody());

        verify(repository).save(argThat(fund -> fund.getBalance() == -100.0));
    }

    @Test
    void testWithdraw_WithOverdrawEnabled_ExceedingLimit_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(100.0);
        fundOpDto.setBalance(700.0);

        when(repository.findById(1L)).thenReturn(Optional.of(testFund));

        FundException exception = assertThrows(FundException.class, () ->
            fundService.withdraw(fundOpDto)
        );
        assertEquals("Attempting to withdraw more than allowed", exception.getMessage());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testWithdraw_WithoutOverdraw_ExceedingBalance_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        testFund.setCanOverdraw(false);
        testFund.setBalance(100.0);
        fundOpDto.setBalance(200.0);

        when(repository.findById(1L)).thenReturn(Optional.of(testFund));

        FundException exception = assertThrows(FundException.class, () ->
            fundService.withdraw(fundOpDto)
        );
        assertEquals("Attempting to withdraw more than available", exception.getMessage());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testWithdraw_NullDto_ThrowsException() {
        FundException exception = assertThrows(FundException.class, () ->
            fundService.withdraw(null)
        );
        assertEquals("No balance to withdraw from", exception.getMessage());
    }

    @Test
    void testWithdraw_ZeroId_ThrowsException() {
        fundOpDto.setId(0L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.withdraw(fundOpDto)
        );
        assertEquals("No balance to withdraw from", exception.getMessage());
    }

    @Test
    void testWithdraw_NegativeAmount_ThrowsException() {
        fundOpDto.setBalance(-100.0);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.withdraw(fundOpDto)
        );
        assertEquals("Wrong value for balance", exception.getMessage());
    }

    @Test
    void testWithdraw_NonExistentFund_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        FundException exception = assertThrows(FundException.class, () ->
            fundService.withdraw(fundOpDto)
        );
        assertEquals("No balance to withdraw from", exception.getMessage());
    }

    @Test
    void testWithdraw_UnauthorizedUser_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(2L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.withdraw(fundOpDto)
        );
        assertEquals("Attempted to access unauthorized funds", exception.getMessage());
    }

    @Test
    void testRequestOverdrawCapabilities_Success() throws FundException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<Banking> response = fundService.requestOverdrawCapabilities(overdrawDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.AUTHORIZED, response.getBody());

        verify(repository).findById(1L);
        verify(repository).save(argThat(fund -> 
            fund.canOverdraw() && fund.getMaxOverdraw() == 500.0
        ));
        verify(bankUserRepository).findById(1L);
        verify(auditService).recordAudit(AuditOperation.OVERDRAW_REQUEST, 0.0, 1000.0, 1000.0, testUser, testFund, null);
    }

    @Test
    void testRequestOverdrawCapabilities_ZeroId_ThrowsException() {
        overdrawDto.setId(0L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.requestOverdrawCapabilities(overdrawDto)
        );
        assertEquals("No funds to overdraw", exception.getMessage());
    }

    @Test
    void testRequestOverdrawCapabilities_NonExistentFund_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        FundException exception = assertThrows(FundException.class, () ->
            fundService.requestOverdrawCapabilities(overdrawDto)
        );
        assertEquals("No funds to overdraw", exception.getMessage());
    }

    @Test
    void testRequestOverdrawCapabilities_UnauthorizedUser_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(2L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.requestOverdrawCapabilities(overdrawDto)
        );
        assertEquals("Attempted to access unauthorized funds", exception.getMessage());
    }

    @Test
    void testRequestOverdrawCapabilities_NonExistentUser_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(bankUserRepository.findById(1L)).thenReturn(Optional.empty());

        FundException exception = assertThrows(FundException.class, () ->
            fundService.requestOverdrawCapabilities(overdrawDto)
        );
        assertEquals("Could not find funds owner", exception.getMessage());
    }

    @Test
    void testCancelOverdrawCapabilities_Success() throws FundException {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(100.0);

        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);
        when(bankUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<Banking> response = fundService.cancelOverdrawCapabilities(overdrawDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.COMPLETED, response.getBody());

        verify(repository).findById(1L);
        verify(repository).save(argThat(fund -> 
            !fund.canOverdraw() && fund.getMaxOverdraw() == 0.0
        ));
        verify(bankUserRepository).findById(1L);
        verify(auditService).recordAudit(AuditOperation.OVERDRAW_CANCEL, 0.0, 100.0, 100.0, testUser, testFund, null);
    }

    @Test
    void testCancelOverdrawCapabilities_NegativeBalance_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(-100.0);

        when(repository.findById(1L)).thenReturn(Optional.of(testFund));

        FundException exception = assertThrows(FundException.class, () ->
            fundService.cancelOverdrawCapabilities(overdrawDto)
        );
        assertEquals("Cannot cancel overdraw when balance is negative", exception.getMessage());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testCancelOverdrawCapabilities_ZeroId_ThrowsException() {
        overdrawDto.setId(0L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.cancelOverdrawCapabilities(overdrawDto)
        );
        assertEquals("No funds overdrawn to cancel", exception.getMessage());
    }

    @Test
    void testCancelOverdrawCapabilities_NonExistentFund_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        FundException exception = assertThrows(FundException.class, () ->
            fundService.cancelOverdrawCapabilities(overdrawDto)
        );
        assertEquals("No funds overdrawn to cancel", exception.getMessage());
    }

    @Test
    void testCancelOverdrawCapabilities_UnauthorizedUser_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(2L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.cancelOverdrawCapabilities(overdrawDto)
        );
        assertEquals("Attempted to access unauthorized funds", exception.getMessage());
    }

    @Test
    void testCancelOverdrawCapabilities_NonExistentUser_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(bankUserRepository.findById(1L)).thenReturn(Optional.empty());

        FundException exception = assertThrows(FundException.class, () ->
            fundService.cancelOverdrawCapabilities(overdrawDto)
        );
        assertEquals("Could not find funds owner", exception.getMessage());
    }

    @Test
    void testDeposit_InvalidCredentials_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn("invalid");

        FundException exception = assertThrows(FundException.class, () ->
            fundService.deposit(fundOpDto)
        );
        assertEquals("An error has occured with the user credentials", exception.getMessage());
    }

    @Test
    void testDeposit_ZeroCredentials_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(0L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.deposit(fundOpDto)
        );
        assertEquals("No authorization for deposits", exception.getMessage());
    }
}