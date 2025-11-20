package com.exalt_company.kata_bank.mapper;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.BankRole;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankUserMapperTest {

    @Test
    void should_map_bank_user_account_to_bank_user_entity() {
        //Given
        UUID id = UUID.randomUUID();
        String name = "John";
        String surname = "Doe";
        String email = "john.doe@test.com";
        String password = "password123";
        BankRole role = BankRole.CLIENT;

        BankUserAccount account = new BankUserAccount(id, name, surname, email, password, role);

        //When
        BankUser bankUser = BankUserMapper.fromDomain(account);

        //Then
        assertThat(bankUser).isNotNull();
        assertThat(bankUser.getId()).isEqualTo(id);
        assertThat(bankUser.getName()).isEqualTo(name);
        assertThat(bankUser.getSurname()).isEqualTo(surname);
        assertThat(bankUser.getEmail()).isEqualTo(email);
        assertThat(bankUser.getPassword()).isEqualTo(password);
        assertThat(bankUser.getRole()).isEqualTo(role);
    }

    @Test
    void should_map_bank_user_entity_to_bank_user_account() {
        //Given
        UUID id = UUID.randomUUID();
        String name = "Jane";
        String surname = "Smith";
        String email = "jane.smith@test.com";
        String password = "password456";
        BankRole role = BankRole.ADMIN;

        BankUser bankUser = new BankUser(id, name, surname, email, password, role);

        //When
        BankUserAccount account = BankUserMapper.toDomain(bankUser);

        //Then
        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(id);
        assertThat(account.getName()).isEqualTo(name);
        assertThat(account.getSurname()).isEqualTo(surname);
        assertThat(account.getEmail()).isEqualTo(email);
        assertThat(account.getPassword()).isEqualTo(password);
        assertThat(account.getRole()).isEqualTo(role);
    }

    @Test
    void should_map_bank_user_with_all_roles() {
        //Given
        UUID id = UUID.randomUUID();
        BankUser clientUser = new BankUser(id, "Client", "User", "client@test.com", "pass1", BankRole.CLIENT);
        BankUser adminUser = new BankUser(UUID.randomUUID(), "Admin", "User", "admin@test.com", "pass2", BankRole.ADMIN);
        BankUser counselorUser = new BankUser(UUID.randomUUID(), "Counselor", "User", "counselor@test.com", "pass3", BankRole.COUNSELOR);

        //When
        BankUserAccount clientAccount = BankUserMapper.toDomain(clientUser);
        BankUserAccount adminAccount = BankUserMapper.toDomain(adminUser);
        BankUserAccount counselorAccount = BankUserMapper.toDomain(counselorUser);

        //Then
        assertThat(clientAccount.getRole()).isEqualTo(BankRole.CLIENT);
        assertThat(adminAccount.getRole()).isEqualTo(BankRole.ADMIN);
        assertThat(counselorAccount.getRole()).isEqualTo(BankRole.COUNSELOR);
    }

    @Test
    void should_map_list_of_bank_users_to_list_of_accounts() {
        //Given
        BankUser user1 = new BankUser(UUID.randomUUID(), "John", "Doe", "john@test.com", "pass1", BankRole.CLIENT);
        BankUser user2 = new BankUser(UUID.randomUUID(), "Jane", "Smith", "jane@test.com", "pass2", BankRole.ADMIN);
        BankUser user3 = new BankUser(UUID.randomUUID(), "Bob", "Johnson", "bob@test.com", "pass3", BankRole.COUNSELOR);
        List<BankUser> users = List.of(user1, user2, user3);

        //When
        List<BankUserAccount> accounts = BankUserMapper.toDomain(users);

        //Then
        assertThat(accounts).hasSize(3);
        assertThat(accounts.get(0).getEmail()).isEqualTo("john@test.com");
        assertThat(accounts.get(1).getEmail()).isEqualTo("jane@test.com");
        assertThat(accounts.get(2).getEmail()).isEqualTo("bob@test.com");
    }

    @Test
    void should_map_empty_list_of_bank_users() {
        //Given
        List<BankUser> users = List.of();

        //When
        List<BankUserAccount> accounts = BankUserMapper.toDomain(users);

        //Then
        assertThat(accounts).isEmpty();
    }

    @Test
    void should_map_single_user_in_list() {
        //Given
        BankUser user = new BankUser(UUID.randomUUID(), "Single", "User", "single@test.com", "pass", BankRole.CLIENT);
        List<BankUser> users = List.of(user);

        //When
        List<BankUserAccount> accounts = BankUserMapper.toDomain(users);

        //Then
        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getEmail()).isEqualTo("single@test.com");
    }

    @Test
    void should_maintain_round_trip_mapping() {
        //Given
        UUID id = UUID.randomUUID();
        String name = "Round";
        String surname = "Trip";
        String email = "round@test.com";
        String password = "password";
        BankRole role = BankRole.CLIENT;

        BankUserAccount originalAccount = new BankUserAccount(id, name, surname, email, password, role);

        //When
        BankUser bankUser = BankUserMapper.fromDomain(originalAccount);
        BankUserAccount mappedAccount = BankUserMapper.toDomain(bankUser);

        //Then
        assertThat(mappedAccount.getId()).isEqualTo(originalAccount.getId());
        assertThat(mappedAccount.getName()).isEqualTo(originalAccount.getName());
        assertThat(mappedAccount.getSurname()).isEqualTo(originalAccount.getSurname());
        assertThat(mappedAccount.getEmail()).isEqualTo(originalAccount.getEmail());
        assertThat(mappedAccount.getPassword()).isEqualTo(originalAccount.getPassword());
        assertThat(mappedAccount.getRole()).isEqualTo(originalAccount.getRole());
    }

    @Test
    void should_map_with_empty_strings() {
        //Given
        UUID id = UUID.randomUUID();
        BankUserAccount account = new BankUserAccount(id, "", "", "", "", BankRole.CLIENT);

        //When
        BankUser bankUser = BankUserMapper.fromDomain(account);

        //Then
        assertThat(bankUser.getName()).isEmpty();
        assertThat(bankUser.getSurname()).isEmpty();
        assertThat(bankUser.getEmail()).isEmpty();
        assertThat(bankUser.getPassword()).isEmpty();
    }
}

