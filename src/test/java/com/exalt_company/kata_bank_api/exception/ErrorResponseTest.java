package com.exalt_company.kata_bank_api.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ErrorResponseTest {

    private void assertErrorResponse(ErrorResponse errorResponse, Date timestamp, int status, String error, String message, String path) {
        assertNotNull(errorResponse);
        assertEquals(timestamp, errorResponse.timestamp());
        assertEquals(status, errorResponse.status());
        assertEquals(error, errorResponse.error());
        assertEquals(message, errorResponse.message());
        assertEquals(path, errorResponse.path());
    }

    @ParameterizedTest
    @CsvSource({
            "400, Bad Request, Invalid input, /api/test",
            "401, Unauthorized, Authentication required, /api/protected",
            "403, Forbidden, Access denied, /api/admin",
            "404, Not Found, Resource not found, /api/users/999",
            "500, Internal Server Error, Unexpected error occurred, /api/process",
            "404, '', '', ''",
            "0, Unknown Error, Something went wrong, /api/unknown",
            "-1, Unknown Error, Something went wrong, /api/unknown",
            "999999, Custom Error, Custom error message, /api/custom",
            "400, Bad Request, This is a very long error message that contains detailed information about what went wrong during the request processing. It should be properly handled and displayed to the user., /api/very/long/path/that/might/be/used/in/real/world/scenarios",
            "400, Bad Request with special chars: !@#$%^&*(), Error message with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?, /api/test?param=value&other=!@#$%^&*()",
            "400, Bad Request with unicode: éèêëàâäôöùûüçñ, Error message with unicode: éèêëàâäôöùûüçñ, /api/test/éèêëàâäôöùûüçñ",
            "400, '   Error with whitespace   ', '   Message with whitespace   ', '   /api/path/with/whitespace   '",
            "400, Error with numbers: 1234567890, Message with numbers: 9876543210, /api/path/with/numbers/1234567890",
            "400, Error with mixed content: 123 ABC !@# éèê 中文, Message with mixed content: 456 DEF $%^ àâä 日本語, /api/path/with/mixed/content/789 GHI &*() ùûü 한국어"
    })
    void testErrorResponseCreation(int status, String error, String message, String path) {
        Date timestamp = new Date();
        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message, path);
        assertErrorResponse(errorResponse, timestamp, status, error, message, path);
    }

    @Test
    void testErrorResponseCreationWithNewLines() {
        Date timestamp = new Date();
        ErrorResponse errorResponse = new ErrorResponse(
                timestamp,
                400,
                "Error\nwith\nnewlines",
                "Message\nwith\nnewlines",
                "/api/path\nwith\nnewlines");
        assertErrorResponse(errorResponse, timestamp, 400,
                "Error\nwith\nnewlines",
                "Message\nwith\nnewlines",
                "/api/path\nwith\nnewlines");
    }

    @Test
    void testErrorResponseCreationWithTabs() {
        Date timestamp = new Date();
        ErrorResponse errorResponse = new ErrorResponse(
                timestamp,
                400,
                "Error\twith\ttabs",
                "Message\twith\ttabs",
                "/api/path\twith\ttabs");
        assertErrorResponse(errorResponse, timestamp,
                400,
                "Error\twith\ttabs",
                "Message\twith\ttabs",
                "/api/path\twith\ttabs");
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
    void testErrorResponseWithVeryLongStrings() {
        Date timestamp = new Date();
        int status = 400;
        String error = "a".repeat(10000);
        String message = "b".repeat(10000);
        String path = "c".repeat(10000);

        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message, path);

        assertNotNull(errorResponse);
        assertEquals(timestamp, errorResponse.timestamp());
        assertEquals(status, errorResponse.status());
        assertEquals(error, errorResponse.error());
        assertEquals(message, errorResponse.message());
        assertEquals(path, errorResponse.path());
    }

    @Test
    void testErrorResponseMultipleInstances() {
        Date timestamp1 = new Date();
        Date timestamp2 = new Date();
        int status1 = 400;
        int status2 = 500;
        String error1 = "First error";
        String error2 = "Second error";
        String message1 = "First message";
        String message2 = "Second message";
        String path1 = "/api/first";
        String path2 = "/api/second";

        ErrorResponse errorResponse1 = new ErrorResponse(timestamp1, status1, error1, message1, path1);
        ErrorResponse errorResponse2 = new ErrorResponse(timestamp2, status2, error2, message2, path2);

        assertNotNull(errorResponse1);
        assertNotNull(errorResponse2);
        assertEquals(timestamp1, errorResponse1.timestamp());
        assertEquals(timestamp2, errorResponse2.timestamp());
        assertEquals(status1, errorResponse1.status());
        assertEquals(status2, errorResponse2.status());
        assertEquals(error1, errorResponse1.error());
        assertEquals(error2, errorResponse2.error());
        assertEquals(message1, errorResponse1.message());
        assertEquals(message2, errorResponse2.message());
        assertEquals(path1, errorResponse1.path());
        assertEquals(path2, errorResponse2.path());
    }

}
