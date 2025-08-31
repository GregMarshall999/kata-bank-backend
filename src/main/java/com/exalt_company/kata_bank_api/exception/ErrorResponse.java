package com.exalt_company.kata_bank_api.exception;

import java.util.Date;

public record ErrorResponse(
    Date timestamp,
    int status,
    String error,
    String message,
    String path)
{}
