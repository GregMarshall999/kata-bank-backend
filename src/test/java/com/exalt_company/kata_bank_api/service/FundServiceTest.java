package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.mapper.FundMapper;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.security.JwtService;
import com.exalt_company.kata_bank_api.service.impl.FundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    private JwtService jwtService;

    @InjectMocks
    private FundService fundService;

    private Fund testFund;
    private String validToken;
    private FundOpDto fundOpDto;
    private OverdrawDto overdrawDto;

    @BeforeEach
    void setUp() {
        BankUser testUser = new BankUser();
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

        validToken = "Bearer valid.jwt.token";
    }

    @Test
    void testRequestOverdrawCapabilitiesSuccess() throws FundException {
        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);

        ResponseEntity<Banking> response = fundService.requestOverdrawCapabilities(overdrawDto, validToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.AUTHORIZED, response.getBody());

        verify(jwtService).extractId(validToken);
        verify(repository).findById(1L);
        verify(repository).save(argThat(fund -> 
            fund.canOverdraw() && fund.getMaxOverdraw() == 500.0
        ));
    }

    @Test
    void testRequestOverdrawCapabilitiesWithNonExistentFund() {
        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        FundException exception = assertThrows(FundException.class, () ->
            fundService.requestOverdrawCapabilities(overdrawDto, validToken)
        );

        assertEquals("No funds to overdraw", exception.getMessage());
        verify(repository).findById(1L);
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testRequestOverdrawCapabilitiesWithZeroId() {
        overdrawDto.setId(0L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.requestOverdrawCapabilities(overdrawDto, validToken)
        );

        assertEquals("No funds to overdraw", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testRequestOverdrawCapabilitiesUnauthorizedUser() {
        when(jwtService.extractId(validToken)).thenReturn(2L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.requestOverdrawCapabilities(overdrawDto, validToken)
        );

        assertEquals("Attempted to access unauthorized funds", exception.getMessage());
        verify(jwtService).extractId(validToken);
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testCancelOverdrawCapabilitiesSuccess() throws FundException {
        // Given
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(100.0);

        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);

        ResponseEntity<Banking> response = fundService.cancelOverdrawCapabilities(overdrawDto, validToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.COMPLETED, response.getBody());

        verify(jwtService).extractId(validToken);
        verify(repository).findById(1L);
        verify(repository).save(argThat(fund -> 
            !fund.canOverdraw() && fund.getMaxOverdraw() == 0.0
        ));
    }

    @Test
    void testCancelOverdrawCapabilitiesWithNegativeBalance() {
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(-100.0);

        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));

        FundException exception = assertThrows(FundException.class, () ->
            fundService.cancelOverdrawCapabilities(overdrawDto, validToken)
        );

        assertEquals("Cannot cancel overdraw when balance is negative", exception.getMessage());
        verify(repository).findById(1L);
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testCancelOverdrawCapabilitiesWithNonExistentFund() {
        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        FundException exception = assertThrows(FundException.class, () ->
            fundService.cancelOverdrawCapabilities(overdrawDto, validToken)
        );

        assertEquals("No funds overdrawn to cancel", exception.getMessage());
        verify(repository).findById(1L);
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testCancelOverdrawCapabilitiesWithZeroId() {
        overdrawDto.setId(0L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.cancelOverdrawCapabilities(overdrawDto, validToken)
        );

        assertEquals("No funds overdrawn to cancel", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testCancelOverdrawCapabilitiesUnauthorizedUser() {
        when(jwtService.extractId(validToken)).thenReturn(2L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.cancelOverdrawCapabilities(overdrawDto, validToken)
        );

        assertEquals("Attempted to access unauthorized funds", exception.getMessage());
        verify(jwtService).extractId(validToken);
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testWithdrawWithOverdrawEnabledSuccess() throws FundException {
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(100.0);
        fundOpDto.setBalance(200.0);

        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);

        ResponseEntity<Banking> response = fundService.withdraw(fundOpDto, validToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.WITHDREW, response.getBody());

        verify(repository).save(argThat(fund -> fund.getBalance() == -100.0));
    }

    @Test
    void testWithdrawWithOverdrawEnabledExceedingLimit() {
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(100.0);
        fundOpDto.setBalance(700.0);

        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));

        FundException exception = assertThrows(FundException.class, () ->
            fundService.withdraw(fundOpDto, validToken)
        );

        assertEquals("Attempting to withdraw more than allowed", exception.getMessage());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testWithdrawWithOverdrawDisabledExceedingBalance() {
        testFund.setCanOverdraw(false);
        testFund.setBalance(100.0);
        fundOpDto.setBalance(200.0);

        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));

        FundException exception = assertThrows(FundException.class, () ->
            fundService.withdraw(fundOpDto, validToken)
        );

        assertEquals("Attempting to withdraw more than available", exception.getMessage());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testWithdrawWithOverdrawEnabledAtLimit() throws FundException {
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(100.0);
        fundOpDto.setBalance(600.0);

        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);

        ResponseEntity<Banking> response = fundService.withdraw(fundOpDto, validToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.WITHDREW, response.getBody());

        verify(repository).save(argThat(fund -> fund.getBalance() == -500.0));
    }

    @Test
    void testWithdrawWithOverdrawEnabledWithinBalance() throws FundException {
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(1000.0);
        fundOpDto.setBalance(300.0);

        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);

        ResponseEntity<Banking> response = fundService.withdraw(fundOpDto, validToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.WITHDREW, response.getBody());

        verify(repository).save(argThat(fund -> fund.getBalance() == 700.0));
    }

    @Test
    void testWithdrawWithOverdrawEnabledZeroBalance() throws FundException {
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(0.0);
        fundOpDto.setBalance(200.0);

        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);

        ResponseEntity<Banking> response = fundService.withdraw(fundOpDto, validToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.WITHDREW, response.getBody());

        verify(repository).save(argThat(fund -> fund.getBalance() == -200.0));
    }

    @Test
    void testWithdrawWithOverdrawEnabledNegativeBalance() throws FundException {
        testFund.setCanOverdraw(true);
        testFund.setMaxOverdraw(500.0);
        testFund.setBalance(-100.0);
        fundOpDto.setBalance(200.0);

        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);

        ResponseEntity<Banking> response = fundService.withdraw(fundOpDto, validToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.WITHDREW, response.getBody());

        verify(repository).save(argThat(fund -> fund.getBalance() == -300.0));
    }

    @Test
    void testRequestOverdrawCapabilitiesWithNullToken() {
        assertThrows(FundException.class, () ->
            fundService.requestOverdrawCapabilities(overdrawDto, null)
        );
    }

    @Test
    void testCancelOverdrawCapabilitiesWithNullToken() {
        assertThrows(FundException.class, () ->
            fundService.cancelOverdrawCapabilities(overdrawDto, null)
        );
    }

    @Test
    void testWithdrawWithNullToken() {
        assertThrows(FundException.class, () ->
            fundService.withdraw(fundOpDto, null)
        );
    }

    @Test
    void testRequestOverdrawCapabilitiesWithNullDto() {
        assertThrows(NullPointerException.class, () ->
            fundService.requestOverdrawCapabilities(null, validToken)
        );
    }

    @Test
    void testCancelOverdrawCapabilitiesWithNullDto() {
        assertThrows(NullPointerException.class, () ->
            fundService.cancelOverdrawCapabilities(null, validToken)
        );
    }

    @Test
    void testWithdrawWithNullDto() {
        FundException exception = assertThrows(FundException.class, () ->
            fundService.withdraw(null, validToken)
        );

        assertEquals("No balance to withdraw from", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testDepositSuccess() throws FundException {
        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testFund));
        when(repository.save(any(Fund.class))).thenReturn(testFund);

        ResponseEntity<Banking> response = fundService.deposit(fundOpDto, validToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Banking.DEPOSITED, response.getBody());

        verify(jwtService).extractId(validToken);
        verify(repository).findById(1L);
        verify(repository).save(argThat(fund -> 
            fund.getBalance() == 1100.0
        ));
    }

    @Test
    void testDepositNewFund() throws FundException {
        fundOpDto.setId(0L);
        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(mapper.toEntity(fundOpDto)).thenReturn(testFund);
        when(repository.save(any(Fund.class))).thenReturn(testFund);

        ResponseEntity<Banking> response = fundService.deposit(fundOpDto, validToken);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(Banking.DEPOSITED, response.getBody());

        verify(jwtService).extractId(validToken);
        verify(repository).save(any(Fund.class));
        verify(repository, never()).findById(any());
    }

    @Test
    void testDepositWithNegativeAmount() {
        fundOpDto.setBalance(-100.0);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.deposit(fundOpDto, validToken)
        );

        assertEquals("Wrong value for balance", exception.getMessage());
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testDepositWithNonExistentFund() {
        when(jwtService.extractId(validToken)).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        FundException exception = assertThrows(FundException.class, () ->
            fundService.deposit(fundOpDto, validToken)
        );

        assertEquals("No balance to add funds", exception.getMessage());
        verify(repository).findById(1L);
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testDepositUnauthorizedUser() {
        when(jwtService.extractId(validToken)).thenReturn(2L);

        FundException exception = assertThrows(FundException.class, () ->
            fundService.deposit(fundOpDto, validToken)
        );

        assertEquals("Attempted to access unauthorized funds", exception.getMessage());
        verify(jwtService).extractId(validToken);
        verify(repository, never()).findById(any());
        verify(repository, never()).save(any(Fund.class));
    }

    @Test
    void testDepositWithNullToken() {
        assertThrows(FundException.class, () ->
            fundService.deposit(fundOpDto, null)
        );
    }

    @Test
    void testDepositWithNullDto() {
        assertThrows(FundException.class, () ->
            fundService.deposit(null, validToken)
        );
    }
}
