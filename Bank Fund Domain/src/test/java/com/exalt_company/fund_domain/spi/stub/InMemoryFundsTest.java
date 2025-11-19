package com.exalt_company.fund_domain.spi.stub;

import com.exalt_company.fund_domain.domain.Fund;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
        Fund created = inMemoryFunds.createFund(ownerId);
        created.setBalance(new BigDecimal("150.0"));

        Fund found = inMemoryFunds.getByOwnerId(ownerId);

        assertThat(found).isSameAs(created);
        assertThat(found.getBalance()).isEqualByComparingTo(new BigDecimal("150.0"));
    }

    @Test
    void shouldThrowWhenNoFundExistsForOwner() {
        UUID ownerId = UUID.randomUUID();

        assertThatThrownBy(() -> inMemoryFunds.getByOwnerId(ownerId))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("No fund for this Owner!");
    }

    @Test
    void shouldThrowWhenMultipleFundsExistForSameOwner() throws FundException {
        UUID ownerId = UUID.randomUUID();
        UUID anotherOwner = UUID.randomUUID();

        inMemoryFunds.createFund(ownerId);
        Fund toUpdate = inMemoryFunds.createFund(anotherOwner);

        inMemoryFunds.updateFund(toUpdate.getId(), fund(ownerId, new BigDecimal("200.0")));

        assertThatThrownBy(() -> inMemoryFunds.getByOwnerId(ownerId))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("Only one fund per owner allowed!");
    }

    @Test
    void shouldUpdateFund() throws FundException {
        UUID initialOwner = UUID.randomUUID();
        Fund persisted = inMemoryFunds.createFund(initialOwner);
        persisted.setBalance(new BigDecimal("100.0"));

        UUID newOwner = UUID.randomUUID();
        Fund updatedData = fund(newOwner, new BigDecimal("600.0"));

        FundStatus status = inMemoryFunds.updateFund(persisted.getId(), updatedData);

        assertThat(status).isEqualTo(FundStatus.SUCCESS);
        Fund updatedFund = inMemoryFunds.getByOwnerId(newOwner);
        assertThat(updatedFund.getBalance()).isEqualByComparingTo(new BigDecimal("600.0"));
        assertThat(updatedFund.getOwnerId()).isEqualTo(newOwner);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingFund() {
        Fund updatedData = fund(UUID.randomUUID(), new BigDecimal("200.0"));

        assertThatThrownBy(() -> inMemoryFunds.updateFund(UUID.randomUUID(), updatedData))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("Fund not found!");
    }

    @Test
    void shouldAssignIdentifierWhenCreatingFund() throws FundException {
        Fund fund = inMemoryFunds.createFund(UUID.randomUUID());
        assertThat(fund.getId()).isNotNull();
    }

    @Test
    void shouldThrowWhenCreatingSecondFundForOwner() throws FundException {
        UUID ownerId = UUID.randomUUID();
        inMemoryFunds.createFund(ownerId);

        assertThatThrownBy(() -> inMemoryFunds.createFund(ownerId))
                .isInstanceOf(FundException.class)
                .hasMessageContaining("Unable to create more than 1 fund per owner!");
    }

    private Fund fund(UUID ownerId, BigDecimal balance) {
        Fund fund = new Fund();
        fund.setOwnerId(ownerId);
        fund.setBalance(balance);
        return fund;
    }
}
