package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.FundDto;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.mapper.FundMapper;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.security.JwtService;
import com.exalt_company.kata_bank_api.service.IFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FundService extends BaseService<FundDto, Fund, FundMapper, FundRepository> implements IFundService {
    private final JwtService jwtService;

    @Autowired
    public FundService(FundMapper mapper, FundRepository repository, JwtService jwtService) {
        super(mapper, repository, Fund.class);
        this.jwtService = jwtService;
    }

    /**
     * This handles depositing.
     * When first deposits are made and when an existing balance needs updating.
     * We check if the user requesting the deposit is the funds owner.
     * Later this can be expanded to admin authority.
     * @param dto
     * @return a deposit status
     * @throws FundException
     */
    @Override
    public ResponseEntity<Banking> deposit(FundDto dto, String token) throws FundException {
        Long id = jwtService.extractId(token);
        if(id == null) throw new FundException("No authorization for deposits");
        if(dto.getOwnerId() != id) throw new FundException("Attempted to access unauthorized funds");

        if(dto.getId() == 0L) {
            repository.save(mapper.toEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(Banking.DEPOSITED);
        }

        Fund found = repository.findById(dto.getId()).orElseThrow(
                () -> new FundException("No balance to add funds"));

        found.setBalance(found.getBalance() + dto.getBalance());
        repository.save(found);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.DEPOSITED);
    }

    @Override
    public ResponseEntity<Banking> withdraw(FundDto dto, String token) throws FundException {
        Long id = jwtService.extractId(token);
        if(id == null) throw new FundException("No authorization for withdrawals");
        if(dto.getOwnerId() != id) throw new FundException("Attempted to access unauthorized funds");

        if(dto.getId() == 0L)
            throw new FundException("No balance to withdraw from");

        Fund found = repository.findById(dto.getId()).orElseThrow(
                () -> new FundException("No balance to withdraw from"));

        if(found.getBalance() - dto.getBalance() < 0)
            throw new FundException("Attempting to withdraw more than available");

        found.setBalance(found.getBalance() - dto.getBalance());
        repository.save(found);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.WITHDREW);
    }
}
