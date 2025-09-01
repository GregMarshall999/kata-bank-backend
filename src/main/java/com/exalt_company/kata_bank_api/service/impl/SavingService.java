package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.SavingException;
import com.exalt_company.kata_bank_api.mapper.SavingMapper;
import com.exalt_company.kata_bank_api.repository.SavingRepository;
import com.exalt_company.kata_bank_api.security.JwtService;
import com.exalt_company.kata_bank_api.service.ISavingService;
import com.exalt_company.kata_bank_api.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SavingService extends BaseService<SavingDto, Saving, SavingMapper, SavingRepository>
        implements ISavingService {
    private final JwtService jwtService;

    @Autowired
    public SavingService(SavingMapper mapper, SavingRepository repository, JwtService jwtService) {
        super(mapper, repository, Saving.class);
        this.jwtService = jwtService;
    }

    /**
     * We assume the saving opening process is completed.
     * Again with a proper email service the system should only be possible via the advisor.
     * @param dto
     * @param token
     * @return
     * @throws SavingException
     */
    @Override
    public ResponseEntity<Banking> openSavingsAccount(SavingDto dto, String token) throws SavingException {
        if(dto.getMaxBalance() < 0) throw new SavingException("Wrong value for max balance");

        ServiceUtil.checkUserAuthorized(token, dto, jwtService, "Savings access unauthorized");

        Saving found = repository.findById(dto.getId()).orElse(null);
        if(found != null) throw new SavingException("Can't open same savings twice");

        repository.save(mapper.toEntity(dto));

        return ResponseEntity.status(HttpStatus.CREATED).body(Banking.AUTHORIZED);
    }

    /**
     * We check the balance before closing the savings in order to not lose currency.
     * @param dto
     * @param token
     * @return
     * @throws SavingException
     */
    @Override
    public ResponseEntity<Banking> closeSavingsAccount(SavingDto dto, String token) throws SavingException {
        ServiceUtil.checkUserAuthorized(token, dto, jwtService, "Savings access unauthorized");

        Saving found = repository.findById(dto.getId())
                .orElseThrow(() -> new SavingException("Could not close non existing savings"));

        if(found.getBalance() != 0) throw new SavingException("Can not close savings if balance not empty");

        repository.delete(mapper.toEntity(dto));

        return ResponseEntity.status(HttpStatus.OK).body(Banking.COMPLETED);
    }

    /**
     * This time deposits require a max balance verification
     * @param dto
     * @param token
     * @return
     * @throws SavingException
     */
    @Override
    public ResponseEntity<Banking> deposit(SavingDto dto, String token) throws SavingException {
        if(dto == null || dto.getId() == 0L) throw new SavingException("Please open a savings account before depositing here");
        if(dto.getBalance() < 0) throw new SavingException("Wrong value for balance");

        ServiceUtil.checkUserAuthorized(token, dto, jwtService, "Savings access unauthorized");

        Saving found = repository.findById(dto.getId())
                .orElseThrow(() -> new SavingException("No savings to deposit"));

        if(found.getBalance() + dto.getBalance() > found.getMaxBalance())
            throw new SavingException("Savings cannot exceed the maximum allowed balance");

        found.setBalance(found.getBalance() + dto.getBalance());

        repository.save(found);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.DEPOSITED);
    }

    /**
     * Similar to funds, we can do withdrawal operations.
     * Savings can not be negative though.
     * @param dto
     * @param token
     * @return
     * @throws SavingException
     */
    @Override
    public ResponseEntity<Banking> withdraw(SavingDto dto, String token) throws SavingException {
        if(dto == null || dto.getId() == 0L) throw new SavingException("No savings to withdraw from");
        if(dto.getBalance() < 0) throw new SavingException("Wrong value for balance");

        ServiceUtil.checkUserAuthorized(token, dto, jwtService, "Savings access unauthorized");

        Saving found = repository.findById(dto.getId())
                .orElseThrow(() -> new SavingException("No savings to withdraw from"));

        if(found.getBalance() - dto.getBalance() < 0)
            throw new SavingException("Attempting to withdraw more than allowed");

        found.setBalance(found.getBalance() - dto.getBalance());
        repository.save(found);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.WITHDREW);
    }
}
