package com.exalt_company.kata_bank_api.exception;

import com.exalt_company.kata_bank_api.enums.Banking;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

/**
 * This project will have many user caused failures.
 * In order to warn them of the issue we have a global handler.
 * This will return a custom message depending on the error encountered to help the user.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex, WebRequest request) {
        return new ResponseEntity<>(buildErrorResponse(ex, request), ex.getStatus());
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, WebRequest request) {
        return new ResponseEntity<>(buildErrorResponse(ex, request), ex.getStatus());
    }

    @ExceptionHandler(FundException.class)
    public ResponseEntity<ErrorResponse> handleFundException(FundException ex, WebRequest request) {
        return new ResponseEntity<>(buildBankingErrorResponse(ex, request), ex.getStatus());
    }

    @ExceptionHandler(SavingException.class)
    public ResponseEntity<ErrorResponse> handleSavingException(SavingException ex, WebRequest request) {
        return new ResponseEntity<>(buildBankingErrorResponse(ex, request), ex.getStatus());
    }

    @ExceptionHandler(AuditException.class)
    public ResponseEntity<ErrorResponse> handleAuditException(AuditException ex, WebRequest request) {
        return new ResponseEntity<>(buildErrorResponse(ex, request), ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleDtoValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        return new ResponseEntity<>(buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(), request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String message = "";
        if (ex.getMessage().contains("Unique index")) message = "Violated a unique field.";

        return new ResponseEntity<>(buildErrorResponse(HttpStatus.BAD_REQUEST, message, request), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private <E extends BankApiException> ErrorResponse buildErrorResponse(E ex, WebRequest request) {
        return new ErrorResponse(
                new Date(),
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String customMessage, WebRequest request) {
        return new ErrorResponse(
                new Date(),
                status.value(),
                status.getReasonPhrase(),
                customMessage,
                request.getDescription(false)
        );
    }

    private <E extends BankApiException> ErrorResponse buildBankingErrorResponse(E ex, WebRequest request) {
        return new ErrorResponse(
                new Date(),
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage() + ": " + Banking.REFUSED,
                request.getDescription(false)
        );
    }
}
