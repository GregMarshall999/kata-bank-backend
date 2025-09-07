package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.fund.FundDto;
import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.service.IFundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
class FundControllerTest {
    @Mock
    private IFundService fundService;

    @InjectMocks
    private FundController fundController;

    private FundDto testFund;
    private FundOpDto testFundOp;
    private OverdrawDto testOverdraw;
    private List<FundDto> testFunds;

    @BeforeEach
    void setUp() {
        testFund = new FundDto();
        testFund.setId(1L);
        testFund.setOwnerId(1L);
        testFund.setBalance(1000.0);
        testFund.setCanOverdraw(false);
        testFund.setMaxOverdraw(0.0);

        testFundOp = new FundOpDto();
        testFundOp.setOwnerId(1L);
        testFundOp.setBalance(100.0);

        testOverdraw = new OverdrawDto();
        testOverdraw.setOwnerId(1L);
        testOverdraw.setMaxOverdraw(500.0);

        FundDto secondFund = new FundDto();
        secondFund.setId(2L);
        secondFund.setOwnerId(2L);
        secondFund.setBalance(2000.0);
        secondFund.setCanOverdraw(true);
        secondFund.setMaxOverdraw(1000.0);

        testFunds = Arrays.asList(testFund, secondFund);
    }

    @Test
    void testCreate_Success() throws Exception {
        when(fundService.create(any(FundDto.class)))
                .thenReturn(ResponseEntity.ok(testFund));

        ResponseEntity<FundDto> response = fundController.create(testFund);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testFund.getId(), response.getBody().getId());
        assertEquals(testFund.getOwnerId(), response.getBody().getOwnerId());
        assertEquals(testFund.getBalance(), response.getBody().getBalance());
        assertEquals(testFund.isCanOverdraw(), response.getBody().isCanOverdraw());
        assertEquals(testFund.getMaxOverdraw(), response.getBody().getMaxOverdraw());

        verify(fundService, times(1)).create(testFund);
    }

    @Test
    void testGetById_Success() throws Exception {
        long fundId = 1L;
        when(fundService.getById(fundId))
                .thenReturn(ResponseEntity.ok(testFund));

        ResponseEntity<FundDto> response = fundController.getById(fundId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testFund.getId(), response.getBody().getId());

        verify(fundService, times(1)).getById(fundId);
    }

    @Test
    void testGetAll_Success() {
        when(fundService.getAll())
                .thenReturn(ResponseEntity.ok(testFunds));

        ResponseEntity<List<FundDto>> response = fundController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(fundService, times(1)).getAll();
    }

    @Test
    void testUpdate_Success() throws Exception {
        long fundId = 1L;
        when(fundService.update(eq(fundId), any(FundDto.class)))
                .thenReturn(ResponseEntity.ok(testFund));

        ResponseEntity<FundDto> response = fundController.update(fundId, testFund);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(fundService, times(1)).update(fundId, testFund);
    }

    @Test
    void testDeleteById_Success() throws Exception {
        long fundId = 1L;
        when(fundService.deleteById(fundId))
                .thenReturn(ResponseEntity.ok(true));

        ResponseEntity<Boolean> response = fundController.deleteById(fundId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(fundService, times(1)).deleteById(fundId);
    }

    @Test
    void testDeposit_Success() throws FundException {
        Banking expectedResult = Banking.DEPOSITED;
        when(fundService.deposit(any(FundOpDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = fundController.deposit(testFundOp);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(fundService, times(1)).deposit(testFundOp);
    }

    @Test
    void testDeposit_WithValidAmount() throws FundException {
        FundOpDto depositOp = new FundOpDto();
        depositOp.setOwnerId(1L);
        depositOp.setBalance(250.75);

        Banking expectedResult = Banking.DEPOSITED;
        when(fundService.deposit(any(FundOpDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = fundController.deposit(depositOp);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(fundService, times(1)).deposit(depositOp);
    }

    @Test
    void testDeposit_ServiceThrowsException() throws FundException {
        String errorMessage = "Fund not found";
        when(fundService.deposit(any(FundOpDto.class)))
                .thenThrow(new FundException(errorMessage, HttpStatus.BAD_REQUEST));

        FundException exception = assertThrows(FundException.class,
                () -> fundController.deposit(testFundOp));

        assertEquals(errorMessage, exception.getMessage());
        verify(fundService, times(1)).deposit(testFundOp);
    }

    @ParameterizedTest
    @CsvSource({
        "0.0, 1, 'Deposit amount must be positive'",
        "-100.0, 1, 'Deposit amount must be positive'",
        "100.0, 0, 'Invalid owner ID'"
    })
    void testDeposit_WithInvalidData(double balance, long ownerId, String expectedErrorMessage) throws FundException {
        FundOpDto invalidOp = new FundOpDto();
        invalidOp.setBalance(balance);
        invalidOp.setOwnerId(ownerId);
        
        when(fundService.deposit(any(FundOpDto.class)))
                .thenThrow(new FundException(expectedErrorMessage, HttpStatus.BAD_REQUEST));

        FundException exception = assertThrows(FundException.class,
                () -> fundController.deposit(invalidOp));

        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(fundService, times(1)).deposit(invalidOp);
    }

    @Test
    void testWithdraw_Success() throws FundException {
        Banking expectedResult = Banking.WITHDREW;
        when(fundService.withdraw(any(FundOpDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = fundController.withdraw(testFundOp);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(fundService, times(1)).withdraw(testFundOp);
    }

    @Test
    void testWithdraw_WithValidAmount() throws FundException {
        FundOpDto withdrawOp = new FundOpDto();
        withdrawOp.setOwnerId(1L);
        withdrawOp.setBalance(50.25);

        Banking expectedResult = Banking.WITHDREW;
        when(fundService.withdraw(any(FundOpDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = fundController.withdraw(withdrawOp);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(fundService, times(1)).withdraw(withdrawOp);
    }

    @Test
    void testWithdraw_ServiceThrowsException() throws FundException {
        String errorMessage = "Insufficient funds";
        when(fundService.withdraw(any(FundOpDto.class)))
                .thenThrow(new FundException(errorMessage, HttpStatus.BAD_REQUEST));

        FundException exception = assertThrows(FundException.class,
                () -> fundController.withdraw(testFundOp));

        assertEquals(errorMessage, exception.getMessage());
        verify(fundService, times(1)).withdraw(testFundOp);
    }

    @ParameterizedTest
    @CsvSource({
        "0.0, 1, 'Withdrawal amount must be positive'",
        "-100.0, 1, 'Withdrawal amount must be positive'"
    })
    void testWithdraw_WithInvalidAmounts(double balance, long ownerId, String expectedErrorMessage) throws FundException {
        FundOpDto invalidOp = new FundOpDto();
        invalidOp.setBalance(balance);
        invalidOp.setOwnerId(ownerId);
        
        when(fundService.withdraw(any(FundOpDto.class)))
                .thenThrow(new FundException(expectedErrorMessage, HttpStatus.BAD_REQUEST));

        FundException exception = assertThrows(FundException.class,
                () -> fundController.withdraw(invalidOp));

        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(fundService, times(1)).withdraw(invalidOp);
    }

    @Test
    void testWithdraw_WithInsufficientFunds() throws FundException {
        testFundOp.setBalance(2000.0);
        String errorMessage = "Insufficient funds";
        when(fundService.withdraw(any(FundOpDto.class)))
                .thenThrow(new FundException(errorMessage, HttpStatus.BAD_REQUEST));

        FundException exception = assertThrows(FundException.class,
                () -> fundController.withdraw(testFundOp));

        assertEquals(errorMessage, exception.getMessage());
        verify(fundService, times(1)).withdraw(testFundOp);
    }

    @Test
    void testRequestOverdrawCapabilities_Success() throws FundException {
        Banking expectedResult = Banking.AUTHORIZED;
        when(fundService.requestOverdrawCapabilities(any(OverdrawDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = fundController.requestOverdrawCapabilities(testOverdraw);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(fundService, times(1)).requestOverdrawCapabilities(testOverdraw);
    }

    @Test
    void testRequestOverdrawCapabilities_WithValidMaxOverdraw() throws FundException {
        OverdrawDto overdrawRequest = new OverdrawDto();
        overdrawRequest.setOwnerId(1L);
        overdrawRequest.setMaxOverdraw(750.0);

        Banking expectedResult = Banking.AUTHORIZED;
        when(fundService.requestOverdrawCapabilities(any(OverdrawDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = fundController.requestOverdrawCapabilities(overdrawRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(fundService, times(1)).requestOverdrawCapabilities(overdrawRequest);
    }

    @Test
    void testRequestOverdrawCapabilities_ServiceThrowsException() throws FundException {
        String errorMessage = "Fund not found";
        when(fundService.requestOverdrawCapabilities(any(OverdrawDto.class)))
                .thenThrow(new FundException(errorMessage, HttpStatus.BAD_REQUEST));

        FundException exception = assertThrows(FundException.class,
                () -> fundController.requestOverdrawCapabilities(testOverdraw));

        assertEquals(errorMessage, exception.getMessage());
        verify(fundService, times(1)).requestOverdrawCapabilities(testOverdraw);
    }

    @ParameterizedTest
    @CsvSource({
        "0.0, 1, 'Max overdraw amount must be positive'",
        "-100.0, 1, 'Max overdraw amount must be positive'",
        "500.0, 0, 'Invalid owner ID'"
    })
    void testRequestOverdrawCapabilities_WithInvalidData(double maxOverdraw, long ownerId, String expectedErrorMessage) throws FundException {
        OverdrawDto invalidOverdraw = new OverdrawDto();
        invalidOverdraw.setMaxOverdraw(maxOverdraw);
        invalidOverdraw.setOwnerId(ownerId);
        
        when(fundService.requestOverdrawCapabilities(any(OverdrawDto.class)))
                .thenThrow(new FundException(expectedErrorMessage, HttpStatus.BAD_REQUEST));

        FundException exception = assertThrows(FundException.class,
                () -> fundController.requestOverdrawCapabilities(invalidOverdraw));

        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(fundService, times(1)).requestOverdrawCapabilities(invalidOverdraw);
    }

    @Test
    void testCancelOverdrawCapabilities_Success() throws FundException {
        Banking expectedResult = Banking.COMPLETED;
        when(fundService.cancelOverdrawCapabilities(any(OverdrawDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = fundController.cancelOverdrawCapabilities(testOverdraw);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(fundService, times(1)).cancelOverdrawCapabilities(testOverdraw);
    }

    @Test
    void testCancelOverdrawCapabilities_ServiceThrowsException() throws FundException {
        String errorMessage = "Fund not found";
        when(fundService.cancelOverdrawCapabilities(any(OverdrawDto.class)))
                .thenThrow(new FundException(errorMessage, HttpStatus.BAD_REQUEST));

        FundException exception = assertThrows(FundException.class,
                () -> fundController.cancelOverdrawCapabilities(testOverdraw));

        assertEquals(errorMessage, exception.getMessage());
        verify(fundService, times(1)).cancelOverdrawCapabilities(testOverdraw);
    }

    @Test
    void testCancelOverdrawCapabilities_WithNegativeBalance() throws FundException {
        String errorMessage = "Cannot cancel overdraw with negative balance";
        when(fundService.cancelOverdrawCapabilities(any(OverdrawDto.class)))
                .thenThrow(new FundException(errorMessage, HttpStatus.BAD_REQUEST));

        FundException exception = assertThrows(FundException.class,
                () -> fundController.cancelOverdrawCapabilities(testOverdraw));

        assertEquals(errorMessage, exception.getMessage());
        verify(fundService, times(1)).cancelOverdrawCapabilities(testOverdraw);
    }

    @Test
    void testCancelOverdrawCapabilities_WithInvalidOwnerId() throws FundException {
        testOverdraw.setOwnerId(0L);
        String errorMessage = "Invalid owner ID";
        when(fundService.cancelOverdrawCapabilities(any(OverdrawDto.class)))
                .thenThrow(new FundException(errorMessage, HttpStatus.BAD_REQUEST));

        FundException exception = assertThrows(FundException.class,
                () -> fundController.cancelOverdrawCapabilities(testOverdraw));

        assertEquals(errorMessage, exception.getMessage());
        verify(fundService, times(1)).cancelOverdrawCapabilities(testOverdraw);
    }

    @Test
    void testDeposit_WithLargeAmount() throws FundException {
        testFundOp.setBalance(999999.99);
        Banking expectedResult = Banking.DEPOSITED;
        when(fundService.deposit(any(FundOpDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = fundController.deposit(testFundOp);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(fundService, times(1)).deposit(testFundOp);
    }

    @Test
    void testWithdraw_WithOverdrawEnabled() throws FundException {
        testFundOp.setBalance(1500.0);
        Banking expectedResult = Banking.WITHDREW;
        when(fundService.withdraw(any(FundOpDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResult));

        ResponseEntity<Banking> response = fundController.withdraw(testFundOp);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        verify(fundService, times(1)).withdraw(testFundOp);
    }

    @Test
    void testWithdraw_ExceedsOverdrawLimit() throws FundException {
        testFundOp.setBalance(2000.0);
        String errorMessage = "Withdrawal exceeds overdraw limit";
        when(fundService.withdraw(any(FundOpDto.class)))
                .thenThrow(new FundException(errorMessage, HttpStatus.BAD_REQUEST));

        FundException exception = assertThrows(FundException.class,
                () -> fundController.withdraw(testFundOp));

        assertEquals(errorMessage, exception.getMessage());
        verify(fundService, times(1)).withdraw(testFundOp);
    }
}
