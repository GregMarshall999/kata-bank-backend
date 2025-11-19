package com.exalt_company.fund_domain.domain;

import com.exalt_company.fund_domain.api.resource.Deposit;
import com.exalt_company.fund_domain.api.resource.Withdraw;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import com.exalt_company.fund_domain.spi.Funds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FundOperatorTest {

    private FundOperator fundOperator;
    private FundsSpy fundsSpy;
    private UUID ownerId;
    private UUID fundId;

    @BeforeEach
    void setUp() {
        fundsSpy = new FundsSpy();
        fundOperator = new FundOperator(fundsSpy);
        ownerId = UUID.randomUUID();
        fundId = UUID.randomUUID();
    }

    @Test
    void shouldDepositIncreaseBalanceAndPersist() throws FundException {
        Fund fund = fund(ownerId, fundId, 100.0);
        fundsSpy.seedFund(fund);

        FundStatus status = fundOperator.deposit(new Deposit(ownerId, fundId, 50.0));

        assertThat(status).isEqualTo(FundStatus.SUCCESS);
        assertThat(fundsSpy.updateFundId).isEqualTo(fundId);
        assertThat(fundsSpy.updatedFund.getBalance()).isEqualTo(150.0);
    }

    @Test
    void shouldWithdrawDecreaseBalanceAndPersist() throws FundException {
        Fund fund = fund(ownerId, fundId, 200.0);
        fundsSpy.seedFund(fund);

        FundStatus status = fundOperator.withdraw(new Withdraw(ownerId, fundId, 80.0));

        assertThat(status).isEqualTo(FundStatus.SUCCESS);
        assertThat(fundsSpy.updatedFund.getBalance()).isEqualTo(120.0);
    }

    @Test
    void shouldThrowWhenWithdrawWouldOverdraw() {
        Fund fund = fund(ownerId, fundId, 60.0);
        fundsSpy.seedFund(fund);

        assertThatThrownBy(() -> fundOperator.withdraw(new Withdraw(ownerId, fundId, 80.0)))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("Balance overdrawn!");
    }

    @Test
    void shouldRejectDepositWithNonPositiveAmount() {
        assertThatThrownBy(() -> fundOperator.deposit(new Deposit(ownerId, fundId, 0)))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("Deposits amounts must be positive!");
    }

    @Test
    void shouldRejectDepositWithoutFundId() {
        assertThatThrownBy(() -> fundOperator.deposit(new Deposit(ownerId, null, 10)))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("Funds are required for deposits!");
    }

    private Fund fund(UUID ownerId, UUID id, double balance) {
        Fund fund = new Fund();
        fund.setOwnerId(ownerId);
        fund.setId(id);
        fund.setBalance(balance);
        return fund;
    }

    private static class FundsSpy implements Funds {
        private Fund storedFund;
        private UUID updateFundId;
        private Fund updatedFund;

        void seedFund(Fund fund) {
            storedFund = fund;
        }

        @Override
        public Fund getByOwnerId(UUID ownerId) throws FundException {
            if (storedFund != null && storedFund.getOwnerId().equals(ownerId)) {
                return storedFund;
            }
            throw new FundException("Fund not found", FundStatus.FAILED);
        }

        @Override
        public FundStatus updateFund(UUID fundId, Fund fundToUpdate) {
            updateFundId = fundId;
            updatedFund = fundToUpdate;
            storedFund = fundToUpdate;
            return FundStatus.SUCCESS;
        }
    }
}