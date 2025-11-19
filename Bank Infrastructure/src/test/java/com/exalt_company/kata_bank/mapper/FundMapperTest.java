package com.exalt_company.kata_bank.mapper;

import com.exalt_company.fund_domain.api.resource.Deposit;
import com.exalt_company.fund_domain.api.resource.Withdraw;
import com.exalt_company.fund_domain.domain.Fund;
import com.exalt_company.kata_bank.adapter.v1.resource.FundRequest;
import com.exalt_company.kata_bank.entity.BankFund;
import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.user_domain.shared.BankRole;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FundMapperTest {

    @Test
    void should_map_fund_request_to_deposit() throws Exception {
        //Given
        UUID fundId = UUID.randomUUID();
        UUID fundOwnerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1000.00");
        FundRequest request = new FundRequest(fundOwnerId, fundId, amount);

        //When
        Deposit deposit = FundMapper.toDomain(request, Deposit.class);

        //Then
        assertThat(deposit).isNotNull();
        assertThat(deposit).isInstanceOf(Deposit.class);
        assertThat(deposit.getFundId()).isEqualTo(fundId);
        assertThat(deposit.getFundOwnerId()).isEqualTo(fundOwnerId);
        assertThat(deposit.getAmount()).isEqualByComparingTo(amount);
    }

    @Test
    void should_map_fund_request_to_withdraw() throws Exception {
        //Given
        UUID fundId = UUID.randomUUID();
        UUID fundOwnerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("500.50");
        FundRequest request = new FundRequest(fundOwnerId, fundId, amount);

        //When
        Withdraw withdraw = FundMapper.toDomain(request, Withdraw.class);

        //Then
        assertThat(withdraw).isNotNull();
        assertThat(withdraw).isInstanceOf(Withdraw.class);
        assertThat(withdraw.getFundId()).isEqualTo(fundId);
        assertThat(withdraw.getFundOwnerId()).isEqualTo(fundOwnerId);
        assertThat(withdraw.getAmount()).isEqualByComparingTo(amount);
    }

    @Test
    void should_map_fund_request_with_zero_amount() throws Exception {
        //Given
        UUID fundId = UUID.randomUUID();
        UUID fundOwnerId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.ZERO;
        FundRequest request = new FundRequest(fundOwnerId, fundId, amount);

        //When
        Deposit deposit = FundMapper.toDomain(request, Deposit.class);

        //Then
        assertThat(deposit).isNotNull();
        assertThat(deposit.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void should_map_fund_request_with_negative_amount() throws Exception {
        //Given
        UUID fundId = UUID.randomUUID();
        UUID fundOwnerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("-100.00");
        FundRequest request = new FundRequest(fundOwnerId, fundId, amount);

        //When
        Withdraw withdraw = FundMapper.toDomain(request, Withdraw.class);

        //Then
        assertThat(withdraw).isNotNull();
        assertThat(withdraw.getAmount()).isEqualByComparingTo(new BigDecimal("-100.00"));
    }

    @Test
    void should_map_fund_request_with_large_amount() throws Exception {
        //Given
        UUID fundId = UUID.randomUUID();
        UUID fundOwnerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("999999999.99");
        FundRequest request = new FundRequest(fundOwnerId, fundId, amount);

        //When
        Deposit deposit = FundMapper.toDomain(request, Deposit.class);

        //Then
        assertThat(deposit).isNotNull();
        assertThat(deposit.getAmount()).isEqualByComparingTo(new BigDecimal("999999999.99"));
    }

    @Test
    void should_map_bank_fund_to_domain_fund() {
        //Given
        UUID fundId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("2500.75");

        BankUser owner = new BankUser(ownerId, "John", "Doe", "john@test.com", "pass", BankRole.CLIENT);
        BankFund bankFund = new BankFund();
        bankFund.setId(fundId);
        bankFund.setBalance(balance);
        bankFund.setOwner(owner);

        //When
        Fund fund = FundMapper.toDomain(bankFund);

        //Then
        assertThat(fund).isNotNull();
        assertThat(fund.getId()).isEqualTo(fundId);
        assertThat(fund.getBalance()).isEqualByComparingTo(balance);
        assertThat(fund.getOwnerId()).isEqualTo(ownerId);
    }

    @Test
    void should_map_bank_fund_with_zero_balance() {
        //Given
        UUID fundId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        BankUser owner = new BankUser(ownerId, "Jane", "Smith", "jane@test.com", "pass", BankRole.CLIENT);
        BankFund bankFund = new BankFund();
        bankFund.setId(fundId);
        bankFund.setBalance(BigDecimal.ZERO);
        bankFund.setOwner(owner);

        //When
        Fund fund = FundMapper.toDomain(bankFund);

        //Then
        assertThat(fund).isNotNull();
        assertThat(fund.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void should_map_bank_fund_with_null_balance() {
        //Given
        UUID fundId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        BankUser owner = new BankUser(ownerId, "Bob", "Johnson", "bob@test.com", "pass", BankRole.CLIENT);
        BankFund bankFund = new BankFund();
        bankFund.setId(fundId);
        bankFund.setBalance(null);
        bankFund.setOwner(owner);

        //When
        Fund fund = FundMapper.toDomain(bankFund);

        //Then
        assertThat(fund).isNotNull();
        assertThat(fund.getId()).isEqualTo(fundId);
        assertThat(fund.getOwnerId()).isEqualTo(ownerId);
        assertThat(fund.getBalance()).isNull();
    }

    @Test
    void should_create_fund_resource_with_correct_parameter_order() throws Exception {
        //Given
        UUID fundId = UUID.randomUUID();
        UUID fundOwnerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("123.45");
        FundRequest request = new FundRequest(fundOwnerId, fundId, amount);

        //When
        Deposit deposit = FundMapper.toDomain(request, Deposit.class);

        //Then - Verify that the parameters are passed in the correct order (fundId, fundOwnerId, amount)
        // The constructor expects (UUID fundId, UUID fundOwnerId, BigDecimal amount)
        // but FundRequest has (fundOwnerId, fundId, amount) - so we need to check the actual mapping
        assertThat(deposit.getFundId()).isEqualTo(fundId);
        assertThat(deposit.getFundOwnerId()).isEqualTo(fundOwnerId);
        assertThat(deposit.getAmount()).isEqualByComparingTo(amount);
    }

    @Test
    void should_map_multiple_fund_resources_from_same_request() throws Exception {
        //Given
        UUID fundId = UUID.randomUUID();
        UUID fundOwnerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1000.00");
        FundRequest request = new FundRequest(fundOwnerId, fundId, amount);

        //When
        Deposit deposit = FundMapper.toDomain(request, Deposit.class);
        Withdraw withdraw = FundMapper.toDomain(request, Withdraw.class);

        //Then
        assertThat(deposit).isNotNull();
        assertThat(withdraw).isNotNull();
        assertThat(deposit.getFundId()).isEqualTo(withdraw.getFundId());
        assertThat(deposit.getFundOwnerId()).isEqualTo(withdraw.getFundOwnerId());
        assertThat(deposit.getAmount()).isEqualByComparingTo(withdraw.getAmount());
    }
}

