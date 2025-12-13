package com.exalt_company.kata_bank.entity;

import com.exalt_company.kata_bank.adapter.v1.resource.TransactionHistoryType;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class BankTransaction extends BaseEntity {
    @NotNull
    private String name;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate date; //TODO: change to LocalDateTime (for hours)

    @NotNull
    private TransactionHistoryType type;

    public BankTransaction() {
    }

    public BankTransaction(String name, BigDecimal amount, LocalDate date, TransactionHistoryType type) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionHistoryType getType() {
        return type;
    }

    public void setType(TransactionHistoryType type) {
        this.type = type;
    }
}
