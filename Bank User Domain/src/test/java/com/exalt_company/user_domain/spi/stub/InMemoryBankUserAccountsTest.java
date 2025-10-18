package com.exalt_company.user_domain.spi.stub;

import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import com.exalt_company.user_domain.spi.BankUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryBankUserAccountsTest {
    private BankUsers bankUsers;

    private BankUserAccount account;

    private final String email = "email@test.com";

    @BeforeEach
    void setUp() {
        bankUsers = new InMemoryBankUserAccounts();

        account = new BankUserAccount("", "", email);
    }

    @Test
    void should_create_account() throws BankUserException {
        BankUserAccount created = bankUsers.createAccount(account, "");

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
    }

    @Test
    void should_fail_creation_if_email_already_exists() throws BankUserException {
        //Given
        bankUsers.createAccount(account, "");

        //Then Expect
        assertThatThrownBy(() -> bankUsers.createAccount(account, ""))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("User with this email already exists");
    }

    @Test
    void should_find_by_email() throws BankUserException {
        //Given
        BankUserAccount created = bankUsers.createAccount(account, "");

        //Then
        BankUserAccount found = bankUsers.findByEmail(email);

        //Expect
        assertThat(found).isNotNull().isSameAs(created);
    }

    @Test
    void should_fail_finding_email_if_non_existent() {
        assertThatThrownBy(() -> bankUsers.findByEmail(email))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("User not found");
    }
}
