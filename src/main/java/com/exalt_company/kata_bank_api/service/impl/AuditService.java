package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.statement.AccountStatementDto;
import com.exalt_company.kata_bank_api.dto.statement.OperationDto;
import com.exalt_company.kata_bank_api.entity.AccountAudit;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.enums.AccountType;
import com.exalt_company.kata_bank_api.enums.AuditOperation;
import com.exalt_company.kata_bank_api.exception.AuditException;
import com.exalt_company.kata_bank_api.repository.AccountAuditRepository;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.repository.SavingRepository;
import com.exalt_company.kata_bank_api.service.IAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService implements IAuditService {
    private final AccountAuditRepository repository;
    private BankUserRepository bankUserRepository;
    private FundRepository fundRepository;
    private SavingRepository savingRepository;

    @Autowired
    public AuditService(AccountAuditRepository repository, BankUserRepository bankUserRepository,
                        FundRepository fundRepository, SavingRepository savingRepository) {
        this.repository = repository;
        this.bankUserRepository = bankUserRepository;
        this.fundRepository = fundRepository;
        this.savingRepository = savingRepository;
    }

    @Override
    public void recordAudit(
            AuditOperation operation, double amount, double balanceBefore, double balanceAfter, BankUser requestingUser,
            Fund userFund, Saving userSaving) {
        AccountAudit audit = new AccountAudit();

        audit.setOperation(operation);
        audit.setAmount(amount);
        audit.setBalanceBefore(balanceBefore);
        audit.setBalanceAfter(balanceAfter);
        audit.setRequestingUser(requestingUser);
        audit.setUserFund(userFund);
        audit.setUserSaving(userSaving);

        repository.save(audit);
    }

    /**
     * We'll pick all the operations for the user account of this month.
     * It is in a page format for ease of display.
     * @param accountType
     * @param ownerId
     * @param page
     * @param size
     * @return
     * @throws AuditException
     */
    @Override
    public ResponseEntity<AccountStatementDto> requestStatement(String accountType, long ownerId, int page, int size)
            throws AuditException {
        try {
            AccountType type = AccountType.valueOf(accountType);

            BankUser owner = bankUserRepository.findById(ownerId)
                    .orElseThrow(() -> new AuditException("Can't find funds owner"));

            AccountStatementDto dto = new AccountStatementDto();
            Page<AccountAudit> audits;
            Fund ownerFunds;
            Saving ownerSavings;
            switch (type) {
                case FUND -> {
                    ownerFunds = fundRepository.findByOwner(owner)
                            .orElseThrow(() -> new AuditException("Could not find user's funds"));
                    audits = repository.findByUserFundOwnerCurrentMonth(owner, PageRequest.of(page, size));
                    dto.setAccountBalance(ownerFunds.getBalance());
                }
                case SAVING -> {
                    ownerSavings = savingRepository.findByOwner(owner)
                            .orElseThrow(() -> new AuditException("Could not find user's Savings"));
                    audits = repository.findByUserSavingOwnerCurrentMonth(owner, PageRequest.of(page, size));
                    dto.setAccountBalance(ownerSavings.getBalance());
                }
                default -> throw new AuditException("A critical error has occurred! Please check accountType value"); //Lets be honest lads, if we ever reach this, pandemonium will follow our doom
            }

            //I might rework this later...
            //Not a fan of how verbal this mapping is.
            //Maybe an idea to set a proper constructor for OperationDto
            List<OperationDto> operations = audits
                    .getContent()
                    .stream()
                    .map(audit -> {
                        OperationDto opDto = new OperationDto();
                        opDto.setOperation(audit.getOperation());
                        opDto.setOperationAuthor(
                                audit.getRequestingUser().getIdentity().getSurname() +
                                        " " +
                                        audit.getRequestingUser().getIdentity().getSurname()
                        );

                        return opDto;
                    })
                    .toList();

            dto.setAccountType(type);
            dto.setOperations(operations);
            dto.setOperationsPage(audits.getNumber());
            dto.setOperationsSize(audits.getSize());
            dto.setTotalOperationsPage(audits.getTotalPages());

            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (IllegalArgumentException e) {
            throw new AuditException("Wrong account type");
        }
    }
}
