package com.exalt_company.kata_bank.adapter.v1.spi;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.repository.BankUserRepository;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.BankRole;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersJpaAdapterTest {

    @Mock
    private BankUserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersJpaAdapter usersJpaAdapter;

    private BankUserAccount userAccount;
    private BankUser bankUser;
    private UUID userId;
    private String email;
    private String password;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        email = "test@example.com";
        password = "password123";
        encodedPassword = "$2a$10$encodedpasswordhash";

        userAccount = new BankUserAccount(userId, "John", "Doe", email, password, BankRole.CLIENT);

        bankUser = new BankUser();
        bankUser.setId(userId);
        bankUser.setName("John");
        bankUser.setSurname("Doe");
        bankUser.setEmail(email);
        bankUser.setPassword(encodedPassword);
        bankUser.setRole(BankRole.CLIENT);
    }

    @Test
    void should_create_account_with_encoded_password() throws BankUserException {
        //Given
        BankUser userToSave = new BankUser();
        userToSave.setId(userId);
        userToSave.setName("John");
        userToSave.setSurname("Doe");
        userToSave.setEmail(email);
        userToSave.setPassword(encodedPassword);
        userToSave.setRole(BankRole.CLIENT);

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);

        //When
        BankUserAccount createdAccount = usersJpaAdapter.createAccount(userAccount);

        //Then
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getId()).isEqualTo(userId);
        assertThat(createdAccount.getEmail()).isEqualTo(email);
        verify(passwordEncoder).encode(password);
        verify(repository).save(any(BankUser.class));
    }

    @Test
    void should_throw_exception_when_creating_account_fails() {
        //Given
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(repository.save(any(BankUser.class))).thenThrow(new IllegalArgumentException("Invalid data"));

        //When/Then
        assertThatThrownBy(() -> usersJpaAdapter.createAccount(userAccount))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("Could not create account");

        verify(passwordEncoder).encode(password);
        verify(repository).save(any(BankUser.class));
    }

    @Test
    void should_throw_exception_on_optimistic_locking_failure() {
        //Given
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(repository.save(any(BankUser.class))).thenThrow(new OptimisticLockingFailureException("Version conflict"));

        //When/Then
        assertThatThrownBy(() -> usersJpaAdapter.createAccount(userAccount))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("Could not create account");

        verify(repository).save(any(BankUser.class));
    }

    @Test
    void should_delete_account_successfully() {
        //Given
        //No exception will be thrown

        //When
        boolean deleted = usersJpaAdapter.deleteAccount(userId);

        //Then
        assertThat(deleted).isTrue();
        verify(repository).deleteById(userId);
    }

    @Test
    void should_return_false_when_delete_fails() {
        //Given
        doThrow(new IllegalArgumentException("Invalid ID")).when(repository).deleteById(userId);

        //When
        boolean deleted = usersJpaAdapter.deleteAccount(userId);

        //Then
        assertThat(deleted).isFalse();
        verify(repository).deleteById(userId);
    }

    @Test
    void should_return_false_on_optimistic_locking_failure_during_delete() {
        //Given
        doThrow(new OptimisticLockingFailureException("Version conflict")).when(repository).deleteById(userId);

        //When
        boolean deleted = usersJpaAdapter.deleteAccount(userId);

        //Then
        assertThat(deleted).isFalse();
        verify(repository).deleteById(userId);
    }

    @Test
    void should_edit_account() throws BankUserException {
        //Given
        BankUserAccount updatedAccount = new BankUserAccount(userId, "Jane", "Smith", "jane@test.com", "newpassword", BankRole.ADMIN);
        BankUser updatedBankUser = new BankUser();
        updatedBankUser.setId(userId);
        updatedBankUser.setName("Jane");
        updatedBankUser.setSurname("Smith");
        updatedBankUser.setEmail("jane@test.com");
        updatedBankUser.setPassword("encodednewpassword");
        updatedBankUser.setRole(BankRole.ADMIN);

        when(repository.findById(userId)).thenReturn(Optional.of(bankUser));
        when(repository.save(any(BankUser.class))).thenReturn(updatedBankUser);

        //When
        BankUserAccount editedAccount = usersJpaAdapter.editAccount(userId, updatedAccount);

        //Then
        assertThat(editedAccount).isNotNull();
        assertThat(editedAccount.getId()).isEqualTo(userId);
        assertThat(editedAccount.getName()).isEqualTo("Jane");
        assertThat(editedAccount.getEmail()).isEqualTo("jane@test.com");
        verify(repository).findById(userId);
        verify(repository).save(any(BankUser.class));
    }

    @Test
    void should_throw_exception_when_editing_with_null_user_id() {
        //Given
        BankUserAccount updatedAccount = new BankUserAccount(null, "Jane", "Smith", "jane@test.com", "newpassword", BankRole.ADMIN);

        //When/Then
        assertThatThrownBy(() -> usersJpaAdapter.editAccount(null, updatedAccount))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("id required");

        verify(repository, never()).findById(any());
        verify(repository, never()).save(any());
    }

    @Test
    void should_throw_exception_when_editing_non_existent_account() {
        //Given
        UUID nonExistentId = UUID.randomUUID();
        BankUserAccount updatedAccount = new BankUserAccount(nonExistentId, "Jane", "Smith", "jane@test.com", "newpassword", BankRole.ADMIN);
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> usersJpaAdapter.editAccount(nonExistentId, updatedAccount))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("does not exist");

        verify(repository).findById(nonExistentId);
        verify(repository, never()).save(any());
    }

    @Test
    void should_throw_exception_on_optimistic_locking_failure_during_edit() {
        //Given
        BankUserAccount updatedAccount = new BankUserAccount(userId, "Jane", "Smith", "jane@test.com", "newpassword", BankRole.ADMIN);
        when(repository.findById(userId)).thenReturn(Optional.of(bankUser));
        when(repository.save(any(BankUser.class))).thenThrow(new OptimisticLockingFailureException("Version conflict"));

        //When/Then
        assertThatThrownBy(() -> usersJpaAdapter.editAccount(userId, updatedAccount))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("Could not edit account");

        verify(repository).save(any(BankUser.class));
    }

    @Test
    void should_find_account_by_email() throws BankUserException {
        //Given
        when(repository.findByEmail(email)).thenReturn(Optional.of(bankUser));

        //When
        BankUserAccount foundAccount = usersJpaAdapter.findByEmail(email);

        //Then
        assertThat(foundAccount).isNotNull();
        assertThat(foundAccount.getId()).isEqualTo(userId);
        assertThat(foundAccount.getEmail()).isEqualTo(email);
        verify(repository).findByEmail(email);
    }

    @Test
    void should_throw_exception_when_finding_by_email_not_found() {
        //Given
        String nonExistentEmail = "nonexistent@test.com";
        when(repository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> usersJpaAdapter.findByEmail(nonExistentEmail))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("User not found");

        verify(repository).findByEmail(nonExistentEmail);
    }

    @Test
    void should_find_account_by_id() throws BankUserException {
        //Given
        when(repository.findById(userId)).thenReturn(Optional.of(bankUser));

        //When
        BankUserAccount foundAccount = usersJpaAdapter.findById(userId);

        //Then
        assertThat(foundAccount).isNotNull();
        assertThat(foundAccount.getId()).isEqualTo(userId);
        verify(repository).findById(userId);
    }

    @Test
    void should_throw_exception_when_finding_by_id_not_found() {
        //Given
        UUID nonExistentId = UUID.randomUUID();
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> usersJpaAdapter.findById(nonExistentId))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("User not found");

        verify(repository).findById(nonExistentId);
    }

    @Test
    void should_page_accounts() throws BankUserException {
        //Given
        int page = 0;
        int size = 10;
        List<BankUser> users = List.of(bankUser);
        PageImpl<BankUser> pageImpl = new PageImpl<>(users, PageRequest.of(page, size), 1);

        when(repository.findAll(PageRequest.of(page, size))).thenReturn(pageImpl);

        //When
        Page<BankUserAccount> result = usersJpaAdapter.pageAccounts(page, size);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.size()).isEqualTo(size);
        verify(repository).findAll(PageRequest.of(page, size));
    }

    @Test
    void should_throw_exception_when_paging_with_negative_page() {
        //Given
        int page = -1;
        int size = 10;

        //When/Then
        assertThatThrownBy(() -> usersJpaAdapter.pageAccounts(page, size))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("Wrong parameter values");

        verify(repository, never()).findAll(any(org.springframework.data.domain.PageRequest.class));
    }

    @Test
    void should_throw_exception_when_paging_with_invalid_size() {
        //Given
        int page = 0;
        int size = 0;

        //When/Then
        assertThatThrownBy(() -> usersJpaAdapter.pageAccounts(page, size))
                .isInstanceOf(BankUserException.class)
                .hasMessageContaining("Wrong parameter values");

        verify(repository, never()).findAll(any(org.springframework.data.domain.PageRequest.class));
    }

    @Test
    void should_page_accounts_with_multiple_pages() throws BankUserException {
        //Given
        int page = 1;
        int size = 5;
        BankUser user2 = new BankUser();
        user2.setId(UUID.randomUUID());
        user2.setEmail("user2@test.com");
        List<BankUser> users = List.of(user2);
        PageImpl<BankUser> pageImpl = new PageImpl<>(users, PageRequest.of(page, size), 6);

        when(repository.findAll(PageRequest.of(page, size))).thenReturn(pageImpl);

        //When
        Page<BankUserAccount> result = usersJpaAdapter.pageAccounts(page, size);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.size()).isEqualTo(size);
        verify(repository).findAll(PageRequest.of(page, size));
    }
}

