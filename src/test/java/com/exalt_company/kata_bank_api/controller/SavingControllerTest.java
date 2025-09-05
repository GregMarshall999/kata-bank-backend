package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.exception.SavingException;
import com.exalt_company.kata_bank_api.service.ISavingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingControllerTest {
    @Mock
    private ISavingService savingService;

    @InjectMocks
    private SavingController savingController;

    private SavingDto testSaving;
    private List<SavingDto> testSavings;

    @BeforeEach
    void setUp() {
        testSaving = new SavingDto();
        testSaving.setId(1L);
        testSaving.setOwnerId(1L);
        testSaving.setBalance(1000.0);
        testSaving.setMaxBalance(10000.0);

        SavingDto secondSaving = new SavingDto();
        secondSaving.setId(2L);
        secondSaving.setOwnerId(2L);
        secondSaving.setBalance(2000.0);
        secondSaving.setMaxBalance(15000.0);

        testSavings = Arrays.asList(testSaving, secondSaving);
    }

    @Test
    void testCreate_Success() throws Exception {
        when(savingService.create(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(testSaving));

        ResponseEntity<SavingDto> response = savingController.create(testSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testSaving.getId(), response.getBody().getId());
        assertEquals(testSaving.getOwnerId(), response.getBody().getOwnerId());
        assertEquals(testSaving.getBalance(), response.getBody().getBalance());
        assertEquals(testSaving.getMaxBalance(), response.getBody().getMaxBalance());

        verify(savingService, times(1)).create(testSaving);
    }

    @Test
    void testGetById_Success() throws Exception {
        long savingId = 1L;
        when(savingService.getById(savingId))
                .thenReturn(ResponseEntity.ok(testSaving));

        ResponseEntity<SavingDto> response = savingController.getById(savingId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testSaving.getId(), response.getBody().getId());

        verify(savingService, times(1)).getById(savingId);
    }

    @Test
    void testGetAll_Success() {
        when(savingService.getAll())
                .thenReturn(ResponseEntity.ok(testSavings));

        ResponseEntity<List<SavingDto>> response = savingController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(savingService, times(1)).getAll();
    }

    @Test
    void testUpdate_Success() throws Exception {
        long savingId = 1L;
        when(savingService.update(eq(savingId), any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(testSaving));

        ResponseEntity<SavingDto> response = savingController.update(savingId, testSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(savingService, times(1)).update(savingId, testSaving);
    }

    @Test
    void testDeleteById_Success() throws Exception {
        long savingId = 1L;
        when(savingService.deleteById(savingId))
                .thenReturn(ResponseEntity.ok(true));

        ResponseEntity<Boolean> response = savingController.deleteById(savingId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(savingService, times(1)).deleteById(savingId);
    }

    @Test
    void testDelete_Success() throws Exception {
        when(savingService.delete(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(true));

        ResponseEntity<Boolean> response = savingController.delete(testSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(savingService, times(1)).delete(testSaving);
    }

    @Test
    void testOpenSavingsAccount_Success() throws SavingException {
        Banking expectedResult = Banking.COMPLETED;
        when(savingService.openSavingsAccount(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = savingController.openSavingsAccount(testSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(savingService, times(1)).openSavingsAccount(testSaving);
    }

    @Test
    void testOpenSavingsAccount_WithValidData() throws SavingException {
        SavingDto newSaving = new SavingDto();
        newSaving.setOwnerId(3L);
        newSaving.setBalance(0.0);
        newSaving.setMaxBalance(5000.0);

        Banking expectedResult = Banking.COMPLETED;
        when(savingService.openSavingsAccount(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = savingController.openSavingsAccount(newSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(savingService, times(1)).openSavingsAccount(newSaving);
    }

    @Test
    void testOpenSavingsAccount_ServiceThrowsException() throws SavingException {
        String errorMessage = "Savings account already exists";
        when(savingService.openSavingsAccount(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.openSavingsAccount(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).openSavingsAccount(testSaving);
    }

    @Test
    void testOpenSavingsAccount_WithNegativeMaxBalance() throws SavingException {
        testSaving.setMaxBalance(-100.0);
        String errorMessage = "Max balance cannot be negative";
        when(savingService.openSavingsAccount(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.openSavingsAccount(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).openSavingsAccount(testSaving);
    }

    @Test
    void testOpenSavingsAccount_WithZeroMaxBalance() throws SavingException {
        testSaving.setMaxBalance(0.0);
        String errorMessage = "Max balance must be positive";
        when(savingService.openSavingsAccount(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.openSavingsAccount(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).openSavingsAccount(testSaving);
    }

    @Test
    void testOpenSavingsAccount_WithInvalidOwnerId() throws SavingException {
        testSaving.setOwnerId(0L);
        String errorMessage = "Invalid owner ID";
        when(savingService.openSavingsAccount(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.openSavingsAccount(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).openSavingsAccount(testSaving);
    }

    @Test
    void testCloseSavingsAccount_Success() throws SavingException {
        Banking expectedResult = Banking.COMPLETED;
        when(savingService.closeSavingsAccount(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = savingController.closeSavingsAccount(testSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(savingService, times(1)).closeSavingsAccount(testSaving);
    }

    @Test
    void testCloseSavingsAccount_ServiceThrowsException() throws SavingException {
        String errorMessage = "Savings account not found";
        when(savingService.closeSavingsAccount(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.closeSavingsAccount(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).closeSavingsAccount(testSaving);
    }

    @Test
    void testCloseSavingsAccount_WithNonZeroBalance() throws SavingException {
        testSaving.setBalance(500.0);
        String errorMessage = "Cannot close account with non-zero balance";
        when(savingService.closeSavingsAccount(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.closeSavingsAccount(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).closeSavingsAccount(testSaving);
    }

    @Test
    void testCloseSavingsAccount_WithInvalidOwnerId() throws SavingException {
        testSaving.setOwnerId(0L);
        String errorMessage = "Invalid owner ID";
        when(savingService.closeSavingsAccount(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.closeSavingsAccount(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).closeSavingsAccount(testSaving);
    }

    @Test
    void testDeposit_Success() throws FundException, SavingException {
        Banking expectedResult = Banking.DEPOSITED;
        when(savingService.deposit(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = savingController.deposit(testSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(savingService, times(1)).deposit(testSaving);
    }

    @Test
    void testDeposit_WithValidAmount() throws FundException, SavingException {
        SavingDto depositSaving = new SavingDto();
        depositSaving.setOwnerId(1L);
        depositSaving.setBalance(250.75);

        Banking expectedResult = Banking.DEPOSITED;
        when(savingService.deposit(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = savingController.deposit(depositSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(savingService, times(1)).deposit(depositSaving);
    }

    @Test
    void testDeposit_ServiceThrowsSavingException() throws FundException, SavingException {
        String errorMessage = "Savings account not found";
        when(savingService.deposit(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.deposit(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).deposit(testSaving);
    }

    @Test
    void testDeposit_ServiceThrowsFundException() throws FundException, SavingException {
        String errorMessage = "Fund operation failed";
        when(savingService.deposit(any(SavingDto.class)))
                .thenThrow(new FundException(errorMessage));

        FundException exception = assertThrows(FundException.class,
                () -> savingController.deposit(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).deposit(testSaving);
    }

    @Test
    void testDeposit_WithZeroAmount() throws FundException, SavingException {
        testSaving.setBalance(0.0);
        String errorMessage = "Deposit amount must be positive";
        when(savingService.deposit(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.deposit(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).deposit(testSaving);
    }

    @Test
    void testDeposit_WithNegativeAmount() throws FundException, SavingException {
        testSaving.setBalance(-100.0);
        String errorMessage = "Deposit amount must be positive";
        when(savingService.deposit(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.deposit(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).deposit(testSaving);
    }

    @Test
    void testDeposit_ExceedsMaxBalance() throws FundException, SavingException {
        testSaving.setBalance(15000.0);
        String errorMessage = "Deposit would exceed maximum balance limit";
        when(savingService.deposit(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.deposit(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).deposit(testSaving);
    }

    @Test
    void testDeposit_WithInvalidOwnerId() throws FundException, SavingException {
        testSaving.setOwnerId(0L);
        String errorMessage = "Invalid owner ID";
        when(savingService.deposit(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.deposit(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).deposit(testSaving);
    }

    @Test
    void testWithdraw_Success() throws SavingException {
        Banking expectedResult = Banking.WITHDREW;
        when(savingService.withdraw(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = savingController.withdraw(testSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(savingService, times(1)).withdraw(testSaving);
    }

    @Test
    void testWithdraw_WithValidAmount() throws SavingException {
        SavingDto withdrawSaving = new SavingDto();
        withdrawSaving.setOwnerId(1L);
        withdrawSaving.setBalance(50.25);

        Banking expectedResult = Banking.WITHDREW;
        when(savingService.withdraw(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = savingController.withdraw(withdrawSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(savingService, times(1)).withdraw(withdrawSaving);
    }

    @Test
    void testWithdraw_ServiceThrowsException() throws SavingException {
        String errorMessage = "Savings account not found";
        when(savingService.withdraw(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.withdraw(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).withdraw(testSaving);
    }

    @Test
    void testWithdraw_WithZeroAmount() throws SavingException {
        testSaving.setBalance(0.0);
        String errorMessage = "Withdrawal amount must be positive";
        when(savingService.withdraw(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.withdraw(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).withdraw(testSaving);
    }

    @Test
    void testWithdraw_WithNegativeAmount() throws SavingException {
        testSaving.setBalance(-100.0);
        String errorMessage = "Withdrawal amount must be positive";
        when(savingService.withdraw(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.withdraw(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).withdraw(testSaving);
    }

    @Test
    void testWithdraw_WithInsufficientBalance() throws SavingException {
        testSaving.setBalance(2000.0);
        String errorMessage = "Insufficient balance";
        when(savingService.withdraw(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.withdraw(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).withdraw(testSaving);
    }

    @Test
    void testWithdraw_WithInvalidOwnerId() throws SavingException {
        testSaving.setOwnerId(0L);
        String errorMessage = "Invalid owner ID";
        when(savingService.withdraw(any(SavingDto.class)))
                .thenThrow(new SavingException(errorMessage));

        SavingException exception = assertThrows(SavingException.class,
                () -> savingController.withdraw(testSaving));

        assertEquals(errorMessage, exception.getMessage());
        verify(savingService, times(1)).withdraw(testSaving);
    }

    @Test
    void testDeposit_WithLargeAmount() throws FundException, SavingException {
        testSaving.setBalance(9999.99);
        Banking expectedResult = Banking.DEPOSITED;
        when(savingService.deposit(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = savingController.deposit(testSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(savingService, times(1)).deposit(testSaving);
    }

    @Test
    void testWithdraw_WithLargeAmount() throws SavingException {
        testSaving.setBalance(999.99);
        Banking expectedResult = Banking.WITHDREW;
        when(savingService.withdraw(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = savingController.withdraw(testSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(savingService, times(1)).withdraw(testSaving);
    }

    @Test
    void testOpenSavingsAccount_WithZeroBalance() throws SavingException {
        testSaving.setBalance(0.0);
        Banking expectedResult = Banking.COMPLETED;
        when(savingService.openSavingsAccount(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = savingController.openSavingsAccount(testSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(savingService, times(1)).openSavingsAccount(testSaving);
    }

    @Test
    void testCloseSavingsAccount_WithZeroBalance() throws SavingException {
        testSaving.setBalance(0.0);
        Banking expectedResult = Banking.COMPLETED;
        when(savingService.closeSavingsAccount(any(SavingDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = savingController.closeSavingsAccount(testSaving);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(savingService, times(1)).closeSavingsAccount(testSaving);
    }
}
