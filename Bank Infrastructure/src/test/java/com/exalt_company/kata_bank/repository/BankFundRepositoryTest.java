package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.BankFund;
import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.user_domain.shared.BankRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BankFundRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BankFundRepository bankFundRepository;

    @Autowired
    private BankUserRepository bankUserRepository;

    private BankUser owner;

    @BeforeEach
    void setUp() {
        owner = new BankUser();
        owner.setName("John");
        owner.setSurname("Doe");
        owner.setEmail("john.doe@test.com");
        owner.setPassword("password123");
        owner.setRole(BankRole.CLIENT);
        owner = entityManager.persistAndFlush(owner);
    }

    @Test
    void should_save_bank_fund() {
        //Given
        BankFund fund = new BankFund();
        fund.setOwner(owner);
        fund.setBalance(new BigDecimal("1000.00"));

        //When
        BankFund saved = bankFundRepository.save(fund);

        //Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getOwner()).isEqualTo(owner);
        assertThat(saved.getBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    void should_find_fund_by_id() {
        //Given
        BankFund fund = new BankFund();
        fund.setOwner(owner);
        fund.setBalance(new BigDecimal("500.00"));
        BankFund saved = entityManager.persistAndFlush(fund);

        //When
        Optional<BankFund> found = bankFundRepository.findById(saved.getId());

        //Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
        assertThat(found.get().getBalance()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    void should_not_find_fund_by_non_existent_id() {
        //When
        Optional<BankFund> found = bankFundRepository.findById(UUID.randomUUID());

        //Then
        assertThat(found).isNotPresent();
    }

    @Test
    void should_find_fund_by_owner_id() {
        //Given
        BankFund fund = new BankFund();
        fund.setOwner(owner);
        fund.setBalance(new BigDecimal("1000.00"));
        BankFund saved = entityManager.persistAndFlush(fund);

        //When
        Optional<BankFund> found = bankFundRepository.findByOwnerId(owner.getId());

        //Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
        assertThat(found.get().getOwner()).isEqualTo(owner);
    }

    @Test
    void should_not_find_fund_by_non_existent_owner_id() {
        //When
        Optional<BankFund> found = bankFundRepository.findByOwnerId(UUID.randomUUID());

        //Then
        assertThat(found).isNotPresent();
    }

    @Test
    void should_find_all_funds() {
        //Given
        BankUser owner2 = new BankUser();
        owner2.setName("Jane");
        owner2.setSurname("Smith");
        owner2.setEmail("jane.smith@test.com");
        owner2.setPassword("password456");
        owner2.setRole(BankRole.CLIENT);
        owner2 = entityManager.persistAndFlush(owner2);

        BankFund fund1 = new BankFund();
        fund1.setOwner(owner);
        fund1.setBalance(new BigDecimal("1000.00"));
        entityManager.persistAndFlush(fund1);

        BankFund fund2 = new BankFund();
        fund2.setOwner(owner2);
        fund2.setBalance(new BigDecimal("2000.00"));
        entityManager.persistAndFlush(fund2);

        //When
        var allFunds = bankFundRepository.findAll();

        //Then
        assertThat(allFunds).hasSize(2);
    }

    @Test
    void should_delete_fund() {
        //Given
        BankFund fund = new BankFund();
        fund.setOwner(owner);
        fund.setBalance(new BigDecimal("1000.00"));
        BankFund saved = entityManager.persistAndFlush(fund);

        //When
        bankFundRepository.delete(saved);
        entityManager.flush();

        //Then
        Optional<BankFund> found = bankFundRepository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }

    @Test
    void should_update_fund() {
        //Given
        BankFund fund = new BankFund();
        fund.setOwner(owner);
        fund.setBalance(new BigDecimal("1000.00"));
        BankFund saved = entityManager.persistAndFlush(fund);

        //When
        saved.setBalance(new BigDecimal("2500.00"));
        BankFund updated = bankFundRepository.save(saved);
        entityManager.flush();

        //Then
        Optional<BankFund> found = bankFundRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getBalance()).isEqualByComparingTo(new BigDecimal("2500.00"));
    }
}

