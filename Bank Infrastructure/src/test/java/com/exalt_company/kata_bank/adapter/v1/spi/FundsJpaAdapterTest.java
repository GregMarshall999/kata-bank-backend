package com.exalt_company.kata_bank.adapter.v1.spi;

import com.exalt_company.fund_domain.domain.Fund;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import com.exalt_company.kata_bank.entity.BankFund;
import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.repository.BankFundRepository;
import com.exalt_company.kata_bank.repository.BankUserRepository;
import com.exalt_company.user_domain.shared.BankRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FundsJpaAdapterTest {

    @Mock
    private BankFundRepository fundRepository;

    @Mock
    private BankUserRepository userRepository;

    @InjectMocks
    private FundsJpaAdapter fundsJpaAdapter;

    private BankUser owner;
    private BankFund bankFund;
    private UUID ownerId;
    private UUID fundId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        fundId = UUID.randomUUID();

        owner = new BankUser();
        owner.setId(ownerId);
        owner.setName("John");
        owner.setSurname("Doe");
        owner.setEmail("john@test.com");
        owner.setPassword("password123");
        owner.setRole(BankRole.CLIENT);

        bankFund = new BankFund();
        bankFund.setId(fundId);
        bankFund.setOwner(owner);
        bankFund.setBalance(new BigDecimal("1000.00"));
    }

    @Test
    void should_create_fund_for_owner() throws FundException {
        //Given
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(fundRepository.save(any(BankFund.class))).thenAnswer(invocation -> {
            BankFund fund = invocation.getArgument(0);
            fund.setId(fundId);
            return fund;
        });

        //When
        Fund createdFund = fundsJpaAdapter.createFund(ownerId);

        //Then
        assertThat(createdFund).isNotNull();
        assertThat(createdFund.getOwnerId()).isEqualTo(ownerId);
        verify(userRepository).findById(ownerId);
        verify(fundRepository).save(any(BankFund.class));
    }

    @Test
    void should_throw_exception_when_owner_not_found() {
        //Given
        UUID nonExistentOwnerId = UUID.randomUUID();
        when(userRepository.findById(nonExistentOwnerId)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> fundsJpaAdapter.createFund(nonExistentOwnerId))
                .isInstanceOf(FundException.class)
                .satisfies(exception -> {
                    FundException fundException = (FundException) exception;
                    assertThat(fundException.getMessage()).contains("Owner not found!");
                    assertThat(fundException.getFundStatus()).isEqualTo(FundStatus.FAILED);
                });

        verify(userRepository).findById(nonExistentOwnerId);
        verify(fundRepository, never()).save(any());
    }

    @Test
    void should_get_fund_by_owner_id() throws FundException {
        //Given
        when(fundRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(bankFund));

        //When
        Fund fund = fundsJpaAdapter.getByOwnerId(ownerId);

        //Then
        assertThat(fund).isNotNull();
        assertThat(fund.getId()).isEqualTo(fundId);
        assertThat(fund.getOwnerId()).isEqualTo(ownerId);
        assertThat(fund.getBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
        verify(fundRepository).findByOwnerId(ownerId);
    }

    @Test
    void should_throw_exception_when_fund_not_found_by_owner_id() {
        //Given
        UUID nonExistentOwnerId = UUID.randomUUID();
        when(fundRepository.findByOwnerId(nonExistentOwnerId)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> fundsJpaAdapter.getByOwnerId(nonExistentOwnerId))
                .isInstanceOf(FundException.class)
                .satisfies(exception -> {
                    FundException fundException = (FundException) exception;
                    assertThat(fundException.getMessage()).contains("Owner has no funds!");
                    assertThat(fundException.getFundStatus()).isEqualTo(FundStatus.FAILED);
                });

        verify(fundRepository).findByOwnerId(nonExistentOwnerId);
    }

    @Test
    void should_update_fund() throws FundException {
        //Given
        Fund fundToUpdate = new Fund(fundId, new BigDecimal("2500.00"), ownerId);
        when(fundRepository.findById(fundId)).thenReturn(Optional.of(bankFund));
        when(fundRepository.save(any(BankFund.class))).thenReturn(bankFund);

        //When
        FundStatus status = fundsJpaAdapter.updateFund(fundId, fundToUpdate);

        //Then
        assertThat(status).isEqualTo(FundStatus.SUCCESS);
        verify(fundRepository).findById(fundId);
        verify(fundRepository).save(any(BankFund.class));
    }

    @Test
    void should_throw_exception_when_fund_not_found_for_update() {
        //Given
        UUID nonExistentFundId = UUID.randomUUID();
        Fund fundToUpdate = new Fund(nonExistentFundId, new BigDecimal("1000.00"), ownerId);
        when(fundRepository.findById(nonExistentFundId)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> fundsJpaAdapter.updateFund(nonExistentFundId, fundToUpdate))
                .isInstanceOf(FundException.class)
                .satisfies(exception -> {
                    FundException fundException = (FundException) exception;
                    assertThat(fundException.getMessage()).contains("Funds not found!");
                    assertThat(fundException.getFundStatus()).isEqualTo(FundStatus.FAILED);
                });

        verify(fundRepository).findById(nonExistentFundId);
        verify(fundRepository, never()).save(any());
    }

    @Test
    void should_throw_exception_when_updating_non_owned_fund() {
        //Given
        UUID differentOwnerId = UUID.randomUUID();
        Fund fundToUpdate = new Fund(fundId, new BigDecimal("1000.00"), differentOwnerId);
        when(fundRepository.findById(fundId)).thenReturn(Optional.of(bankFund));

        //When/Then
        assertThatThrownBy(() -> fundsJpaAdapter.updateFund(fundId, fundToUpdate))
                .isInstanceOf(FundException.class)
                .satisfies(exception -> {
                    FundException fundException = (FundException) exception;
                    assertThat(fundException.getMessage()).contains("Attempted to edit non owned funds!");
                    assertThat(fundException.getFundStatus()).isEqualTo(FundStatus.UNAUTHORIZED);
                });

        verify(fundRepository).findById(fundId);
        verify(fundRepository, never()).save(any());
    }

    @Test
    void should_throw_exception_when_fund_id_mismatch() {
        //Given
        UUID differentFundId = UUID.randomUUID();
        Fund fundToUpdate = new Fund(differentFundId, new BigDecimal("1000.00"), ownerId);
        when(fundRepository.findById(fundId)).thenReturn(Optional.of(bankFund));

        //When/Then
        assertThatThrownBy(() -> fundsJpaAdapter.updateFund(fundId, fundToUpdate))
                .isInstanceOf(FundException.class)
                .satisfies(exception -> {
                    FundException fundException = (FundException) exception;
                    assertThat(fundException.getMessage()).contains("Fund ID mismatch!");
                    assertThat(fundException.getFundStatus()).isEqualTo(FundStatus.UNAUTHORIZED);
                });

        verify(fundRepository).findById(fundId);
        verify(fundRepository, never()).save(any());
    }

    @Test
    void should_update_fund_with_zero_balance() throws FundException {
        //Given
        Fund fundToUpdate = new Fund(fundId, BigDecimal.ZERO, ownerId);
        when(fundRepository.findById(fundId)).thenReturn(Optional.of(bankFund));
        when(fundRepository.save(any(BankFund.class))).thenReturn(bankFund);

        //When
        FundStatus status = fundsJpaAdapter.updateFund(fundId, fundToUpdate);

        //Then
        assertThat(status).isEqualTo(FundStatus.SUCCESS);
        verify(fundRepository).save(any(BankFund.class));
    }

    @Test
    void should_update_fund_with_null_balance() throws FundException {
        //Given
        Fund fundToUpdate = new Fund(fundId, null, ownerId);
        when(fundRepository.findById(fundId)).thenReturn(Optional.of(bankFund));
        when(fundRepository.save(any(BankFund.class))).thenReturn(bankFund);

        //When
        FundStatus status = fundsJpaAdapter.updateFund(fundId, fundToUpdate);

        //Then
        assertThat(status).isEqualTo(FundStatus.SUCCESS);
        verify(fundRepository).save(any(BankFund.class));
    }
}

