package com.exalt_company.transaction_domain.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private String name;
    private BigDecimal amount;
    private LocalDateTime date;
    private TransactionType type;

    public Transaction(String name, BigDecimal amount, LocalDateTime date, TransactionType type) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
