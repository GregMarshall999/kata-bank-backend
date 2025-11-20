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

        account = new BankUserAccount("", "", email, "");
    }

    @Test
    void should_create_account() throws BankUserException {
        BankUserAccount created = bankUsers.createAccount(account);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
    }

    @Test
    void should_fail_creation_if_email_already_exists() throws BankUserException {
        //Given
        bankUsers.createAccount(account);

        //Then Expect
        assertThatThrownBy(() -> bankUsers.createAccount(account))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("User with this email already exists");
    }

    @Test
    void should_find_by_email() throws BankUserException {
        //Given
        BankUserAccount created = bankUsers.createAccount(account);

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

    @Test
    void should_find_by_id() throws BankUserException {
        //Given
        BankUserAccount created = bankUsers.createAccount(account);

        //Then
        BankUserAccount found = bankUsers.findById(created.getId());

        //Expect
        assertThat(found).isNotNull().isSameAs(created);
    }

    @Test
    void should_fail_finding_id_if_non_existent() {
        assertThatThrownBy(() -> bankUsers.findById(java.util.UUID.randomUUID()))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void should_delete_account() throws BankUserException {
        //Given
        BankUserAccount created = bankUsers.createAccount(account);

        //When
        boolean deleted = bankUsers.deleteAccount(created.getId());

        //Then
        assertThat(deleted).isTrue();
        assertThatThrownBy(() -> bankUsers.findById(created.getId()))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void should_return_false_when_deleting_non_existent_account() {
        //When
        boolean deleted = bankUsers.deleteAccount(java.util.UUID.randomUUID());

        //Then
        assertThat(deleted).isFalse();
    }

    @Test
    void should_edit_account() throws BankUserException {
        //Given
        BankUserAccount created = bankUsers.createAccount(account);
        BankUserAccount updatedAccount = new BankUserAccount("NewName", "NewSurname", "newemail@test.com", "newpassword");
        updatedAccount.setId(created.getId());

        //When
        BankUserAccount edited = bankUsers.editAccount(created.getId(), updatedAccount);

        //Then
        assertThat(edited).isNotNull();
        assertThat(edited.getName()).isEqualTo("NewName");
        assertThat(edited.getSurname()).isEqualTo("NewSurname");
        assertThat(edited.getEmail()).isEqualTo("newemail@test.com");
    }

    @Test
    void should_fail_editing_non_existent_account() {
        //Given
        BankUserAccount updatedAccount = new BankUserAccount("Name", "Surname", "email@test.com", "password");

        //Then Expect
        assertThatThrownBy(() -> bankUsers.editAccount(java.util.UUID.randomUUID(), updatedAccount))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("Could not edit non existing user");
    }

    @Test
    void should_page_accounts() throws BankUserException {
        //Given - create 5 accounts
        for (int i = 0; i < 5; i++) {
            BankUserAccount acc = new BankUserAccount("Name" + i, "Surname" + i, "email" + i + "@test.com", "password");
            bankUsers.createAccount(acc);
        }

        //When
        com.exalt_company.user_domain.shared.Page<BankUserAccount> page = bankUsers.pageAccounts(0, 3);

        //Then
        assertThat(page).isNotNull();
        assertThat(page.content()).hasSize(3);
        assertThat(page.page()).isZero();
        assertThat(page.size()).isEqualTo(3);
    }

    @Test
    void should_return_empty_page_when_no_accounts_exist() throws BankUserException {
        //When
        com.exalt_company.user_domain.shared.Page<BankUserAccount> page = bankUsers.pageAccounts(0, 10);

        //Then
        assertThat(page).isNotNull();
        assertThat(page.content()).isEmpty();
    }

    @Test
    void should_return_partial_page_when_fewer_accounts_than_page_size() throws BankUserException {
        //Given - create 2 accounts
        for (int i = 0; i < 2; i++) {
            BankUserAccount acc = new BankUserAccount("Name" + i, "Surname" + i, "email" + i + "@test.com", "password");
            bankUsers.createAccount(acc);
        }

        //When
        com.exalt_company.user_domain.shared.Page<BankUserAccount> page = bankUsers.pageAccounts(0, 5);

        //Then
        assertThat(page).isNotNull();
        assertThat(page.content()).hasSize(2);
    }

    @Test
    void should_return_second_page_correctly() throws BankUserException {
        //Given - create 7 accounts
        for (int i = 0; i < 7; i++) {
            BankUserAccount acc = new BankUserAccount("Name" + i, "Surname" + i, "email" + i + "@test.com", "password");
            bankUsers.createAccount(acc);
        }

        //When
        com.exalt_company.user_domain.shared.Page<BankUserAccount> page = bankUsers.pageAccounts(1, 3);

        //Then
        assertThat(page).isNotNull();
        assertThat(page.content()).hasSize(3);
        assertThat(page.page()).isEqualTo(1);
    }

    @Test
    void should_fail_paging_with_negative_page_number() {
        assertThatThrownBy(() -> bankUsers.pageAccounts(-1, 10))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("Wrong value for parameters");
    }

    @Test
    void should_fail_paging_with_invalid_size() {
        assertThatThrownBy(() -> bankUsers.pageAccounts(0, 0))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("Wrong value for parameters");
    }
}
