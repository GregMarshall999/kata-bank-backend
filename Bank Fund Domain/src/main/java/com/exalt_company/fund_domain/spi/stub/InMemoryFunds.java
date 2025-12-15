package com.exalt_company.fund_domain.spi.stub;

import com.exalt_company.fund_domain.domain.Fund;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import com.exalt_company.fund_domain.spi.Funds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemoryFunds implements Funds {
    private final Map<UUID, Fund> funds = new HashMap<>();

    @Override
    public Fund createFund(UUID ownerId) throws FundException {
        List<Fund> existingFunds = funds.values()
                .stream()
                .filter(fund -> fund.getOwnerId().equals(ownerId)).toList();

        if(!existingFunds.isEmpty())
            throw new FundException("Unable to create more than 1 fund per owner!", FundStatus.FAILED);

        UUID uuid = UUID.randomUUID();
        while (funds.containsKey(uuid))
            uuid = UUID.randomUUID();

        Fund fund = new Fund(uuid, ownerId);
        funds.put(uuid, fund);

        return fund;
    }

    @Override
    public Fund getByOwnerId(UUID ownerId) throws FundException {
        List<Fund> found = funds.values()
                .stream()
                .filter(fund -> fund.getOwnerId().equals(ownerId)).toList();

        if(found.isEmpty())
            throw new FundException("No fund for this Owner!", FundStatus.FAILED);

        if(found.size() != 1)
            throw new FundException("Only one fund per owner allowed!", FundStatus.FAILED);

        return found.get(0);
    }

    @Override
    public FundStatus updateFund(UUID fundId, Fund fundToUpdate) throws FundException {
        Fund found = funds.get(fundId);

        if(found == null)
            throw new FundException("Fund not found!", FundStatus.FAILED);

        found.setBalance(fundToUpdate.getBalance());
        found.setOwnerId(fundToUpdate.getOwnerId());

        return FundStatus.SUCCESS;
    }

    @Override
    public Fund getById(UUID fundId) throws FundException {
        return null;
    }
}
