package com.exalt_company.fund_domain.spi.stub;

import com.exalt_company.fund_domain.domain.Fund;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryFundsTest {

    private InMemoryFunds inMemoryFunds;

    @BeforeEach
    void setUp() {
        inMemoryFunds = new InMemoryFunds();
    }

    @Test
    void shouldGetFundByOwnerId() throws FundException {
        UUID ownerId = UUID.randomUUID();
        Fund fund = fund(ownerId, 150.0);

        inMemoryFunds.createFund(fund);

        Fund found = inMemoryFunds.getByOwnerId(ownerId);

        assertThat(found).isSameAs(fund);
        assertThat(found.getBalance()).isEqualTo(150.0);
    }

    @Test
    void shouldThrowWhenNoFundExistsForOwner() {
        UUID ownerId = UUID.randomUUID();

        assertThatThrownBy(() -> inMemoryFunds.getByOwnerId(ownerId))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("No fund for this Owner!");
    }

    @Test
    void shouldThrowWhenMultipleFundsExistForSameOwner() {
        UUID ownerId = UUID.randomUUID();

        inMemoryFunds.createFund(fund(ownerId, 250.0));
        inMemoryFunds.createFund(fund(ownerId, 500.0));

        assertThatThrownBy(() -> inMemoryFunds.getByOwnerId(ownerId))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("Only one fund per owner allowed!");
    }

    @Test
    void shouldUpdateFund() throws FundException {
        UUID initialOwner = UUID.randomUUID();
        inMemoryFunds.createFund(fund(initialOwner, 100.0));
        Fund persisted = inMemoryFunds.getByOwnerId(initialOwner);

        UUID newOwner = UUID.randomUUID();
        Fund updatedData = fund(newOwner, 600.0);

        FundStatus status = inMemoryFunds.updateFund(persisted.getId(), updatedData);

        assertThat(status).isEqualTo(FundStatus.SUCCESS);
        Fund updatedFund = inMemoryFunds.getByOwnerId(newOwner);
        assertThat(updatedFund.getBalance()).isEqualTo(600.0);
        assertThat(updatedFund.getOwnerId()).isEqualTo(newOwner);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingFund() {
        Fund updatedData = fund(UUID.randomUUID(), 200.0);

        assertThatThrownBy(() -> inMemoryFunds.updateFund(UUID.randomUUID(), updatedData))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("Fund not found!");
    }

    @Test
    void shouldAssignIdentifierWhenCreatingFund() {
        Fund fund = fund(UUID.randomUUID(), 75.0);

        inMemoryFunds.createFund(fund);

        assertThat(fund.getId()).isNotNull();
    }

    private Fund fund(UUID ownerId, double balance) {
        Fund fund = new Fund();
        fund.setOwnerId(ownerId);
        fund.setBalance(balance);
        return fund;
    }
}
