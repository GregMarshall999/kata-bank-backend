package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.FundDto;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.mapper.FundMapper;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.service.IFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FundService extends BaseService<FundDto, Fund, FundMapper, FundRepository> implements IFundService {
    @Autowired
    public FundService(FundMapper mapper, FundRepository repository) {
        super(mapper, repository, Fund.class);
    }

    /**
     * This handles depositing.
     * When first deposits are made and when an existing balance needs updating.
     * @param dto
     * @return a deposit status
     * @throws FundException on any deposit refusal
     */
    @Override
    public ResponseEntity<Banking> deposit(FundDto dto) throws FundException {
        if(dto.getId() == 0L) {
            repository.save(mapper.toEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(Banking.DEPOSITED);
        }

        Fund found = repository.findById(dto.getId()).orElseThrow(
                () -> new FundException("No balance to add funds", Banking.REFUSED));

        found.setBalance(found.getBalance() + dto.getBalance());
        repository.save(found);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.DEPOSITED);
    }

    @Override
    public ResponseEntity<Banking> withdraw(FundDto dto) throws FundException {
        if(dto.getId() == 0L)
            throw new FundException("No balance to withdraw from", Banking.REFUSED);

        Fund found = repository.findById(dto.getId()).orElseThrow(
                () -> new FundException("No balance to withdraw from", Banking.REFUSED));

        if(found.getBalance() - dto.getBalance() < 0)
            throw new FundException("Attempting to withdraw more than available", Banking.REFUSED);

        found.setBalance(found.getBalance() - dto.getBalance());
        repository.save(found);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.WITHDREW);
    }
}
