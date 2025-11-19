package com.exalt_company.fund_domain.api;

import com.exalt_company.fund_domain.api.resource.Deposit;
import com.exalt_company.fund_domain.api.resource.Withdraw;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;

public interface FundAction {
    FundStatus deposit(Deposit deposit) throws FundException;
    FundStatus withdraw(Withdraw withdraw) throws FundException;
}
