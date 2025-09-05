package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.entity.AccountAudit;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.enums.AuditOperation;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.SavingException;
import com.exalt_company.kata_bank_api.mapper.SavingMapper;
import com.exalt_company.kata_bank_api.repository.AccountAuditRepository;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.repository.SavingRepository;
import com.exalt_company.kata_bank_api.service.IAuditService;
import com.exalt_company.kata_bank_api.service.ISavingService;
import com.exalt_company.kata_bank_api.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingService extends BaseService<SavingDto, Saving, SavingMapper, SavingRepository>
        implements ISavingService {
    private final BankUserRepository bankUserRepository;
    private final AccountAuditRepository accountAuditRepository;
    private final IAuditService auditService;

    @Autowired
    public SavingService(
            SavingMapper mapper, SavingRepository repository, BankUserRepository bankUserRepository,
            AccountAuditRepository accountAuditRepository, IAuditService auditService) {
        super(mapper, repository, Saving.class);
        this.bankUserRepository = bankUserRepository;
        this.accountAuditRepository = accountAuditRepository;
        this.auditService = auditService;
    }

    /**
     * We assume the saving opening process is completed.
     * Again with a proper email service the system should only be possible via the advisor.
     * @param dto
     * @return
     * @throws SavingException
     */
    @Override
    public ResponseEntity<Banking> openSavingsAccount(SavingDto dto) throws SavingException {
        if(dto.getMaxBalance() < 0) throw new SavingException("Wrong value for max balance");

        ServiceUtil.checkUserAuthorized(dto, "Savings access unauthorized");

        Saving found = repository.findById(dto.getId()).orElse(null);
        if(found != null) throw new SavingException("Can't open same savings twice");

        BankUser savingsOwner = bankUserRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new SavingException("Could not find savings owner"));

        if(repository.findByOwner(savingsOwner).isPresent()) throw new SavingException("User can only have one savings account");

        Saving savedSavings = repository.save(mapper.toEntity(dto));

        auditService.recordAudit(
                AuditOperation.OPEN, savedSavings.getBalance(), 0D, savedSavings.getBalance(), savingsOwner, null,
                savedSavings);

        return ResponseEntity.status(HttpStatus.CREATED).body(Banking.AUTHORIZED);
    }

    /**
     * We check the balance before closing the savings in order to not lose currency.
     * @param dto
     * @return
     * @throws SavingException
     */
    @Override
    public ResponseEntity<Banking> closeSavingsAccount(SavingDto dto) throws SavingException {
        if(dto == null) throw new SavingException("Could not close savings. No savings to close.");

        ServiceUtil.checkUserAuthorized(dto, "Savings access unauthorized");

        Saving found = repository.findById(dto.getId())
                .orElseThrow(() -> new SavingException("Could not close non existing savings"));

        if(found.getBalance() != 0) throw new SavingException("Can not close savings if balance not empty");

        BankUser savingsOwner = bankUserRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new SavingException("Could not find savings owner"));

        List<AccountAudit> allSavingAudits = accountAuditRepository.findAllByUserSaving(found);
        allSavingAudits.forEach(accountAudit -> {
            accountAudit.setUserSaving(null);
            accountAuditRepository.save(accountAudit);
        });

        repository.delete(found);

        auditService.recordAudit(
                AuditOperation.CLOSE, 0D, 0D, 0D, savingsOwner, null,
                null);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.COMPLETED);
    }

    /**
     * This time deposits require a max balance verification
     * @param dto
     * @return
     * @throws SavingException
     */
    @Override
    public ResponseEntity<Banking> deposit(SavingDto dto) throws SavingException {
        if(dto == null || dto.getId() == 0L) throw new SavingException("Please open a savings account before depositing here");
        if(dto.getBalance() < 0) throw new SavingException("Wrong value for balance");

        ServiceUtil.checkUserAuthorized(dto, "Savings access unauthorized");

        Saving found = repository.findById(dto.getId())
                .orElseThrow(() -> new SavingException("No savings to deposit"));

        double before = found.getBalance();
        double after = found.getBalance() + dto.getBalance();

        if(after > found.getMaxBalance())
            throw new SavingException("Savings cannot exceed the maximum allowed balance");

        BankUser savingsOwner = bankUserRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new SavingException("Could not find savings owner"));

        found.setBalance(after);

        Saving savedSavings = repository.save(found);

        auditService.recordAudit(
                AuditOperation.DEPOSIT, dto.getBalance(), before, after, savingsOwner, null, savedSavings);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.DEPOSITED);
    }

    /**
     * Similar to funds, we can do withdrawal operations.
     * Savings can not be negative though.
     * @param dto
     * @return
     * @throws SavingException
     */
    @Override
    public ResponseEntity<Banking> withdraw(SavingDto dto) throws SavingException {
        if(dto == null || dto.getId() == 0L) throw new SavingException("No savings to withdraw from");
        if(dto.getBalance() < 0) throw new SavingException("Wrong value for balance");

        ServiceUtil.checkUserAuthorized(dto, "Savings access unauthorized");

        Saving found = repository.findById(dto.getId())
                .orElseThrow(() -> new SavingException("No savings to withdraw from"));

        double before = found.getBalance();
        double after = found.getBalance() - dto.getBalance();
        if(after < 0)
            throw new SavingException("Attempting to withdraw more than allowed");

        BankUser savingsOwner = bankUserRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new SavingException("Could not find savings owner"));

        found.setBalance(after);
        Saving savedSavings = repository.save(found);

        auditService.recordAudit(
                AuditOperation.WITHDRAW, dto.getBalance(), before, after, savingsOwner, null, savedSavings);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.WITHDREW);
    }
}
