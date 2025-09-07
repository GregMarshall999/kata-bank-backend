package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.fund.FundDto;
import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.enums.AuditOperation;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.mapper.FundMapper;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.service.IAuditService;
import com.exalt_company.kata_bank_api.service.IFundService;
import com.exalt_company.kata_bank_api.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FundService extends BaseService<FundDto, Fund, FundMapper, FundRepository> implements IFundService {
    private final BankUserRepository bankUserRepository;
    private final IAuditService auditService;

    @Autowired
    public FundService(
            FundMapper mapper, FundRepository repository, BankUserRepository bankUserRepository,
            IAuditService auditService) {
        super(mapper, repository, Fund.class);
        this.bankUserRepository = bankUserRepository;
        this.auditService = auditService;
    }

    /**
     * This handles depositing.
     * When first deposits are made and when an existing balance needs updating.
     * We check if the user requesting the deposit is the funds' owner.
     * Later this can be expanded to admin authority.
     * @param dto
     * @return a deposit status
     * @throws FundException
     */
    @Override
    public ResponseEntity<Banking> deposit(FundOpDto dto) throws FundException {
        if(dto == null) throw new FundException("Wrong request body", HttpStatus.BAD_REQUEST);
        if(dto.getBalance() < 0) throw new FundException("Wrong value for balance", HttpStatus.BAD_REQUEST);

        ServiceUtil.checkUserAuthorized(dto, "No authorization for deposits");

        BankUser fundsOwner = bankUserRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new FundException("Could not find funds owner", HttpStatus.BAD_REQUEST));

        if(dto.getId() == 0L && repository.findByOwner(fundsOwner).isPresent()) throw new FundException("User can only have one funds account", HttpStatus.BAD_REQUEST);

        double before;
        double after;
        Fund savedFunds;
        HttpStatus status;
        if(dto.getId() == 0L) {
            savedFunds = repository.save(mapper.toEntity(dto));
            before = 0;
            after = savedFunds.getBalance();
            status = HttpStatus.CREATED;
        }
        else {
            Fund found = repository.findById(dto.getId()).orElseThrow(
                    () -> new FundException("No balance to add funds", HttpStatus.BAD_REQUEST));

            before = found.getBalance();
            after = found.getBalance() + dto.getBalance();

            found.setBalance(after);
            savedFunds = repository.save(found);
            status = HttpStatus.OK;
        }

        auditService.recordAudit(
                AuditOperation.DEPOSIT, dto.getBalance(), before, after, fundsOwner, savedFunds, null);

        return ResponseEntity.status(status).body(Banking.DEPOSITED);
    }

    /**
     * We now have the ability to overdraw the balance.
     * As long as the result does not exceed the authorized overdraw value.
     * These operations are available only if the fund has the overdraw enabled feature active.
     * @param dto
     * @return
     * @throws FundException
     */
    @Override
    public ResponseEntity<Banking> withdraw(FundOpDto dto) throws FundException {
        if(dto == null || dto.getId() == 0L) throw new FundException("No balance to withdraw from", HttpStatus.BAD_REQUEST);
        if(dto.getBalance() < 0) throw new FundException("Wrong value for balance", HttpStatus.BAD_REQUEST);

        ServiceUtil.checkUserAuthorized(dto, "No authorization for withdrawals");

        Fund found = repository.findById(dto.getId()).orElseThrow(
                () -> new FundException("No balance to withdraw from", HttpStatus.BAD_REQUEST));

        double before = found.getBalance();
        double after = found.getBalance() - dto.getBalance();

        if(!found.canOverdraw() && after < 0)
            throw new FundException("Attempting to withdraw more than available", HttpStatus.BAD_REQUEST);

        if(found.canOverdraw() && after < -found.getMaxOverdraw())
            throw new FundException("Attempting to withdraw more than allowed", HttpStatus.BAD_REQUEST);

        BankUser fundsOwner = bankUserRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new FundException("Could not find funds owner", HttpStatus.BAD_REQUEST));

        found.setBalance(after);
        Fund savedFunds = repository.save(found);

        auditService.recordAudit(
                AuditOperation.WITHDRAW, dto.getBalance(), before, after, fundsOwner, savedFunds, null);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.WITHDREW);
    }

    /**
     * We can simulate a contact system with the advisor in order to activate the overdraw feature.
     * For now we assume the request is always authorized
     * @param overdrawDto
     * @return
     */
    @Override
    public ResponseEntity<Banking> requestOverdrawCapabilities(OverdrawDto overdrawDto) throws FundException {
        if(overdrawDto.getId() == 0L) throw new FundException("No funds to overdraw", HttpStatus.BAD_REQUEST);

        ServiceUtil.checkUserAuthorized(overdrawDto, "No authorization for overdraws");

        Fund found = repository.findById(overdrawDto.getId())
                .orElseThrow(() -> new FundException("No funds to overdraw", HttpStatus.BAD_REQUEST));

        found.setCanOverdraw(true);
        found.setMaxOverdraw(overdrawDto.getMaxOverdraw());

        BankUser fundsOwner = bankUserRepository.findById(overdrawDto.getOwnerId())
                .orElseThrow(() -> new FundException("Could not find funds owner", HttpStatus.BAD_REQUEST));

        Fund savedFunds = repository.save(found);

        auditService.recordAudit(
                AuditOperation.OVERDRAW_REQUEST, 0D, found.getBalance(), savedFunds.getBalance(), fundsOwner, savedFunds,
                null);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.AUTHORIZED);
    }

    /**
     * Before canceling the overdraw operations, the user's balance must not be negative.
     * @param overdrawDto
     * @return
     * @throws FundException
     */
    @Override
    public ResponseEntity<Banking> cancelOverdrawCapabilities(OverdrawDto overdrawDto) throws FundException {
        if(overdrawDto.getId() == 0L) throw new FundException("No funds overdrawn to cancel", HttpStatus.BAD_REQUEST);

        ServiceUtil.checkUserAuthorized(overdrawDto, "No authorization for overdraw cancellation");

        Fund found = repository.findById(overdrawDto.getId())
                .orElseThrow(() -> new FundException("No funds overdrawn to cancel", HttpStatus.BAD_REQUEST));

        if(!found.canOverdraw()) throw new FundException("Overdraw is not enabled", HttpStatus.BAD_REQUEST);

        if(found.getBalance() < 0)
            throw new FundException("Cannot cancel overdraw when balance is negative", HttpStatus.BAD_REQUEST);

        found.setMaxOverdraw(0D);
        found.setCanOverdraw(false);

        BankUser fundsOwner = bankUserRepository.findById(overdrawDto.getOwnerId())
                .orElseThrow(() -> new FundException("Could not find funds owner", HttpStatus.BAD_REQUEST));

        Fund savedFunds = repository.save(found);

        auditService.recordAudit(
                AuditOperation.OVERDRAW_CANCEL, 0D, found.getBalance(), savedFunds.getBalance(), fundsOwner, savedFunds,
                null);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.COMPLETED);
    }
}
