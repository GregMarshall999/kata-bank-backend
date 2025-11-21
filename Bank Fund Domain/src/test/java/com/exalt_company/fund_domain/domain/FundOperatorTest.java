package com.exalt_company.fund_domain.domain;

import com.exalt_company.fund_domain.api.resource.Deposit;
import com.exalt_company.fund_domain.api.resource.Withdraw;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import com.exalt_company.fund_domain.spi.Funds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
        Fund fund = fund(ownerId, fundId, new BigDecimal("100.0"));
        fundsSpy.seedFund(fund);

        FundStatus status = fundOperator.deposit(new Deposit(fundId, ownerId, new BigDecimal("50.0")));

        assertThat(status).isEqualTo(FundStatus.SUCCESS);
        assertThat(fundsSpy.updateFundId).isEqualTo(fundId);
        assertThat(fundsSpy.updatedFund.getBalance()).isEqualByComparingTo(new BigDecimal("150.0"));
    }

    @Test
    void shouldCreateFundWhenDepositingForNewOwner() throws FundException {
        FundStatus status = fundOperator.deposit(new Deposit(fundId, ownerId, new BigDecimal("40.0")));

        assertThat(status).isEqualTo(FundStatus.CREATED);
        assertThat(fundsSpy.createFundOwnerId).isEqualTo(ownerId);
        assertThat(fundsSpy.updatedFund.getBalance()).isEqualByComparingTo(new BigDecimal("40.0"));
    }

    @Test
    void shouldWithdrawDecreaseBalanceAndPersist() throws FundException {
        Fund fund = fund(ownerId, fundId, new BigDecimal("200.0"));
        fundsSpy.seedFund(fund);

        FundStatus status = fundOperator.withdraw(new Withdraw(fundId, ownerId, new BigDecimal("80.0")));

        assertThat(status).isEqualTo(FundStatus.SUCCESS);
        assertThat(fundsSpy.updatedFund.getBalance()).isEqualByComparingTo(new BigDecimal("120.0"));
    }

    @Test
    void shouldThrowWhenWithdrawWouldOverdraw() {
        Fund fund = fund(ownerId, fundId, new BigDecimal("60.0"));
        fundsSpy.seedFund(fund);

        assertThatThrownBy(() -> fundOperator.withdraw(new Withdraw(fundId, ownerId, new BigDecimal("80.0"))))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("Balance overdrawn!");
    }

    @Test
    void shouldRejectDepositWithNonPositiveAmount() {
        assertThatThrownBy(() -> fundOperator.deposit(new Deposit(fundId, ownerId, BigDecimal.ZERO)))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("Deposits amounts must be positive!");
    }

    private Fund fund(UUID ownerId, UUID id, BigDecimal balance) {
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
        private UUID createFundOwnerId;

        void seedFund(Fund fund) {
            storedFund = fund;
        }

        @Override
        public Fund createFund(UUID ownerId) {
            createFundOwnerId = ownerId;
            storedFund = new Fund();
            storedFund.setOwnerId(ownerId);
            storedFund.setId(UUID.randomUUID());
            storedFund.setBalance(BigDecimal.ZERO);
            return storedFund;
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