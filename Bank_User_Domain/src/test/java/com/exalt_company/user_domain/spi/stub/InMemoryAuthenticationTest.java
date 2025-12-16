package com.exalt_company.user_domain.spi.stub;

import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;
import com.exalt_company.user_domain.spi.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryAuthenticationTest {
    private Authentication<String> authentication;

    private BankUserAccount account;

    private final UUID id = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        authentication = new InMemoryAuthentication();

        account = new BankUserAccount("", "", "", "password");
    }

    @Test
    void should_generate_token_on_signup() throws AuthenticationException {
        account.setId(id);

        String token = authentication.generateNewUserToken(account);

        assertThat(token).isEqualTo("stub-token-string");
    }

    @Test
    void should_fail_signup_when_no_user_id() {
        assertThatThrownBy(() -> authentication.generateNewUserToken(account))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Unable to generate token: Account non existent");
    }

    @Test
    void should_generate_token_on_sign_in() throws AuthenticationException {
        //Given
        account.setId(id);
        authentication.generateNewUserToken(account);

        //Then
        String token = authentication.generateLoginUserToken(account);

        //Expect
        assertThat(token).isEqualTo("stub-token-string");
    }

    @Test
    void should_fail_sign_in_when_no_user_id() {
        assertThatThrownBy(() -> authentication.generateLoginUserToken(account))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Unable to generate token: Account non existent");
    }

    @Test
    void should_fail_sign_in_when_wrong_password() throws AuthenticationException {
        //Given
        account.setId(id);
        authentication.generateNewUserToken(account);

        BankUserAccount testAccount = new BankUserAccount("", "", "", "wrong-password");
        testAccount.setId(id);

        //Then Expect
        assertThatThrownBy(() -> authentication.generateLoginUserToken(testAccount))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Unable to generate token: Wrong Credentials");
    }
}
