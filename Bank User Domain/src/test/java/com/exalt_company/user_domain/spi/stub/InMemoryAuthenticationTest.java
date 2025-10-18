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

    private final String password = "password";
    private final UUID id = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        authentication = new InMemoryAuthentication();

        account = new BankUserAccount("", "", "");
    }

    @Test
    void should_generate_token_on_signup() throws AuthenticationException {
        account.setId(id);

        String token = authentication.generateNewUserToken(account, password);

        assertThat(token).isEqualTo("stub-token-string");
    }

    @Test
    void should_fail_signup_when_no_user_id() {
        assertThatThrownBy(() -> authentication.generateNewUserToken(account, password))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Unable to generate token: Account non existent");
    }

    @Test
    void should_generate_token_on_signin() throws AuthenticationException {
        //Given
        account.setId(id);
        authentication.generateNewUserToken(account, password);

        //Then
        String token = authentication.generateLoginUserToken(account, password);

        //Expect
        assertThat(token).isEqualTo("stub-token-string");
    }

    @Test
    void should_fail_signin_when_no_user_id() {
        assertThatThrownBy(() -> authentication.generateLoginUserToken(account, password))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Unable to generate token: Account non existent");
    }

    @Test
    void should_fail_signin_when_wrong_password() throws AuthenticationException {
        //Given
        account.setId(id);
        authentication.generateNewUserToken(account, password);

        //Then Expect
        assertThatThrownBy(() -> authentication.generateLoginUserToken(account, "wrong password"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Unable to generate token: Wrong Credentials");
    }
}
