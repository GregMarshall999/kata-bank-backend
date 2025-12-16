package com.exalt_company.kata_bank.adapter.v1.spi;

import com.exalt_company.fund_domain.domain.Fund;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import com.exalt_company.fund_domain.spi.Funds;
import com.exalt_company.kata_bank.entity.BankFund;
import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.mapper.FundMapper;
import com.exalt_company.kata_bank.repository.BankUserRepository;
import com.exalt_company.kata_bank.repository.BankFundRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA adapter implementation of the Funds SPI.
 * Provides persistence operations for fund accounts using JPA repositories.
 */
@Component
public class FundsJpaAdapter implements Funds {
    private final BankFundRepository repository;
    private final BankUserRepository userRepository;

    public FundsJpaAdapter(BankFundRepository repository, BankUserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public Fund createFund(UUID ownerId) throws FundException {
        BankUser owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new FundException("Owner not found!", FundStatus.FAILED));

        BankFund ownerFund = new BankFund(BigDecimal.ZERO, owner);
        BankFund savedFund = repository.save(ownerFund);

        return FundMapper.toDomain(savedFund);
    }

    @Override
    public Fund getByOwnerId(UUID ownerId) throws FundException {
        BankFund ownerFund = repository.findByOwnerId(ownerId)
                .orElseThrow(() -> new FundException("Owner has no funds!", FundStatus.FAILED));

        return FundMapper.toDomain(ownerFund);
    }

    @Override
    public Fund getById(UUID fundId) throws FundException {
        BankFund bankFund = repository.findById(fundId)
                .orElseThrow(() -> new FundException("No funds found!", FundStatus.FAILED));

        return FundMapper.toDomain(bankFund);
    }

    @Override
    public FundStatus updateFund(UUID fundId, Fund fundToUpdate) throws FundException {
        BankFund bankFund = repository.findById(fundId)
                .orElseThrow(() -> new FundException("Funds not found!", FundStatus.FAILED));

        if(!fundToUpdate.getOwnerId().equals(bankFund.getOwner().getId()))
            throw new FundException("Attempted to edit non owned funds!", FundStatus.UNAUTHORIZED);
        if(!fundToUpdate.getId().equals(bankFund.getId()))
            throw new FundException("Fund ID mismatch!", FundStatus.UNAUTHORIZED);

        bankFund.setBalance(fundToUpdate.getBalance());
        repository.save(bankFund);

        return FundStatus.SUCCESS;
    }
}
