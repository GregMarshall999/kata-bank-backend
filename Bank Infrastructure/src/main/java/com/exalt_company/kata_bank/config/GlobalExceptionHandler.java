package com.exalt_company.kata_bank.config;

import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import com.exalt_company.kata_bank.adapter.v1.resource.ErrorResponse;
import com.exalt_company.user_domain.shared.exception.AccountComunicationException;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import com.exalt_company.user_domain.shared.exception.MessageException;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for all REST controllers.
 * Provides centralized exception handling and consistent error responses.
 */
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles FundException from fund operations.
     * Returns 400 for general errors, 409 for conflicts (e.g., refused operations).
     */
    @ExceptionHandler(FundException.class)
    public ResponseEntity<ErrorResponse> handleFundException(FundException ex) {
        logger.warn("Fund operation failed: {}", ex.getMessage());
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        FundStatus fundStatus = ex.getFundStatus();
        
        // Map specific fund statuses to appropriate HTTP status codes
        if (fundStatus == FundStatus.REFUSED) {
            status = HttpStatus.CONFLICT; // For insufficient funds or refused operations
        } else if (fundStatus == FundStatus.UNAUTHORIZED) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (fundStatus == FundStatus.FAILED) {
            status = HttpStatus.BAD_REQUEST;
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "Fund operation failed with status: " + fundStatus
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Handles AuthenticationException from authentication operations.
     * Returns 401 Unauthorized.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                "Authentication or authorization failed"
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handles BankUserException from user account operations.
     * Returns 400 Bad Request or 404 Not Found based on the error message.
     */
    @ExceptionHandler(BankUserException.class)
    public ResponseEntity<ErrorResponse> handleBankUserException(BankUserException ex) {
        logger.warn("Bank user operation failed: {}", ex.getMessage());
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage();
        
        // Check if it's a "not found" scenario
        if (message != null && (message.contains("not found") || message.contains("does not exist"))) {
            status = HttpStatus.NOT_FOUND;
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                message,
                "Bank user operation failed"
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Handles AccountComunicationException from communication operations.
     * Returns 400 Bad Request.
     */
    @ExceptionHandler(AccountComunicationException.class)
    public ResponseEntity<ErrorResponse> handleAccountComunicationException(AccountComunicationException ex) {
        logger.warn("Account communication operation failed: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Account communication operation failed"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles MessageException from message operations.
     * Returns 400 Bad Request.
     */
    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorResponse> handleMessageException(MessageException ex) {
        logger.warn("Message operation failed: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Message operation failed"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles validation errors from @Valid annotations.
     * Returns 400 Bad Request with validation error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Validation failed: {}", ex.getMessage());
        
        List<String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                validationErrors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles IllegalArgumentException for invalid method arguments.
     * Returns 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Invalid argument provided"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles generic RuntimeException.
     * Returns 500 Internal Server Error or 404 Not Found based on context.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();
        
        // Check if it's a "not found" scenario (e.g., refresh token not found)
        if (message != null && (message.contains("not found") || message.contains("does not exist"))) {
            status = HttpStatus.NOT_FOUND;
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                message != null ? message : "An unexpected error occurred",
                "Internal server error"
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Handles all other unhandled exceptions.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

