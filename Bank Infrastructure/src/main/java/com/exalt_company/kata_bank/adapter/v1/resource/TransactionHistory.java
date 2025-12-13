package com.exalt_company.kata_bank.adapter.v1.resource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistory(String name, BigDecimal amount, LocalDateTime date, TransactionHistoryType type) {}
