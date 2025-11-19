package com.exalt_company.kata_bank.service;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.repository.BankUserRepository;
import com.exalt_company.user_domain.shared.BankRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private BankUserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private BankUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new BankUser();
        testUser.setId(UUID.randomUUID());
        testUser.setName("John");
        testUser.setSurname("Doe");
        testUser.setEmail("john.doe@test.com");
        testUser.setPassword("password123");
        testUser.setRole(BankRole.CLIENT);
    }

    @Test
    void should_load_user_by_username() {
        //Given
        String email = "john.doe@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        //When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        //Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isEqualTo(testUser);
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.getAuthorities()).isNotEmpty();
        verify(userRepository).findByEmail(email);
    }

    @Test
    void should_throw_exception_when_user_not_found() {
        //Given
        String email = "nonexistent@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: " + email);
        
        verify(userRepository).findByEmail(email);
    }

    @Test
    void should_load_user_with_different_roles() {
        //Given
        testUser.setRole(BankRole.ADMIN);
        String email = "admin@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        //When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        //Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ADMIN");
    }

    @Test
    void should_load_user_with_client_role() {
        //Given
        testUser.setRole(BankRole.CLIENT);
        String email = "client@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        //When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        //Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("CLIENT");
    }

    @Test
    void should_load_user_with_counselor_role() {
        //Given
        testUser.setRole(BankRole.COUNSELOR);
        String email = "counselor@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        //When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        //Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("COUNSELOR");
    }

    @Test
    void should_load_user_with_case_sensitive_email() {
        //Given
        String email = "John.Doe@Test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        //When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        //Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("john.doe@test.com");
        verify(userRepository).findByEmail(email);
    }

    @Test
    void should_throw_exception_with_correct_message() {
        //Given
        String email = "missing@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: " + email);
    }

    @Test
    void should_return_user_details_with_all_required_fields() {
        //Given
        String email = "john.doe@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        //When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        //Then
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isNotNull();
        assertThat(userDetails.getAuthorities()).isNotEmpty();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void should_handle_empty_email_string() {
        //Given
        String email = "";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: ");
    }
}

