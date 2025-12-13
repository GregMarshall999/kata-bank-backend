package com.exalt_company.kata_bank.adapter.v1.resource;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionHistory(String name, BigDecimal amount, LocalDate date, TransactionHistoryType type) {}
