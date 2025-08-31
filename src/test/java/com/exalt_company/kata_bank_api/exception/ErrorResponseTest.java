package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ErrorResponseTest {

    @Test
    void testErrorResponseCreation() {
        Date timestamp = new Date();
        int status = 400;
        String error = "Bad Request";
        String message = "Invalid input";
        String path = "/api/test";

        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message, path);

        assertNotNull(errorResponse);
        assertEquals(timestamp, errorResponse.timestamp());
        assertEquals(status, errorResponse.status());
        assertEquals(error, errorResponse.error());
        assertEquals(message, errorResponse.message());
        assertEquals(path, errorResponse.path());
    }

    @Test
    void testErrorResponseWithNullValues() {
        Date timestamp = new Date();
        int status = 500;
        String error = "Internal Server Error";
        String message = null;
        String path = null;

        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message, path);

        assertNotNull(errorResponse);
        assertEquals(timestamp, errorResponse.timestamp());
        assertEquals(status, errorResponse.status());
        assertEquals(error, errorResponse.error());
        assertNull(errorResponse.message());
        assertNull(errorResponse.path());
    }

    @Test
    void testErrorResponseWithEmptyStrings() {
        Date timestamp = new Date();
        int status = 404;
        String error = "";
        String message = "";
        String path = "";

        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message, path);

        assertNotNull(errorResponse);
        assertEquals(timestamp, errorResponse.timestamp());
        assertEquals(status, errorResponse.status());
        assertEquals("", errorResponse.error());
        assertEquals("", errorResponse.message());
        assertEquals("", errorResponse.path());
    }

    @Test
    void testErrorResponseWithZeroStatus() {
        Date timestamp = new Date();
        int status = 0;
        String error = "Unknown Error";
        String message = "Something went wrong";
        String path = "/api/unknown";

        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message, path);

        assertNotNull(errorResponse);
        assertEquals(timestamp, errorResponse.timestamp());
        assertEquals(0, errorResponse.status());
        assertEquals(error, errorResponse.error());
        assertEquals(message, errorResponse.message());
        assertEquals(path, errorResponse.path());
    }

    @Test
    void testErrorResponseWithLongMessage() {
        Date timestamp = new Date();
        int status = 400;
        String error = "Bad Request";
        String message = "This is a very long error message that contains detailed information about what went wrong during the request processing. It should be properly handled and displayed to the user.";
        String path = "/api/very/long/path/that/might/be/used/in/real/world/scenarios";

        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message, path);

        assertNotNull(errorResponse);
        assertEquals(timestamp, errorResponse.timestamp());
        assertEquals(status, errorResponse.status());
        assertEquals(error, errorResponse.error());
        assertEquals(message, errorResponse.message());
        assertEquals(path, errorResponse.path());
    }
}
