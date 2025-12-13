package com.exalt_company.kata_bank.adapter.v1.api;

import com.exalt_company.kata_bank.adapter.v1.resource.TransactionHistory;
import com.exalt_company.kata_bank.mapper.TransactionMapper;
import com.exalt_company.transaction_domain.api.TransactionReport;
import com.exalt_company.transaction_domain.shared.TransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionReport transactionReport;

    public TransactionController(TransactionReport transactionReport) {
        this.transactionReport = transactionReport;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TransactionHistory>> getUserTransactions(UUID userId) throws TransactionException {
        List<TransactionHistory> transactions = TransactionMapper
                .fromDomain(transactionReport.getUserTransactionHistory(userId));

        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }
}
