package com.exalt_company.fund_domain.spi;

import com.exalt_company.fund_domain.domain.Fund;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;

import java.util.UUID;

public interface Funds {
    Fund createFund(UUID ownerId) throws FundException;
    Fund getByOwnerId(UUID ownerId) throws FundException;
    FundStatus updateFund(UUID fundId, Fund fundToUpdate) throws FundException;
}
