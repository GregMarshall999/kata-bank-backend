package com.exalt_company.kata_bank.mapper;

import com.exalt_company.kata_bank.adapter.v1.resource.AuthRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.SignUpRequest;
import com.exalt_company.user_domain.api.resource.authentication.SignInUser;
import com.exalt_company.user_domain.api.resource.authentication.SignUpUser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthMapperTest {

    @Test
    void should_map_auth_request_to_sign_in_user() {
        //Given
        String email = "john.doe@test.com";
        String password = "password123";
        AuthRequest request = new AuthRequest(email, password);

        //When
        SignInUser signInUser = AuthMapper.toDomain(request);

        //Then
        assertThat(signInUser).isNotNull();
        assertThat(signInUser.email()).isEqualTo(email);
        assertThat(signInUser.password()).isEqualTo(password);
    }

    @Test
    void should_map_auth_request_with_empty_fields() {
        //Given
        AuthRequest request = new AuthRequest("", "");

        //When
        SignInUser signInUser = AuthMapper.toDomain(request);

        //Then
        assertThat(signInUser).isNotNull();
        assertThat(signInUser.email()).isEmpty();
        assertThat(signInUser.password()).isEmpty();
    }

    @Test
    void should_map_sign_up_request_to_sign_up_user() {
        //Given
        String name = "John";
        String surname = "Doe";
        String email = "john.doe@test.com";
        String password = "password123";
        SignUpRequest request = new SignUpRequest(name, surname, email, password);

        //When
        SignUpUser signUpUser = AuthMapper.toDomain(request);

        //Then
        assertThat(signUpUser).isNotNull();
        assertThat(signUpUser.name()).isEqualTo(name);
        assertThat(signUpUser.surname()).isEqualTo(surname);
        assertThat(signUpUser.email()).isEqualTo(email);
        assertThat(signUpUser.password()).isEqualTo(password);
    }

    @Test
    void should_map_sign_up_request_with_all_fields() {
        //Given
        SignUpRequest request = new SignUpRequest("Jane", "Smith", "jane.smith@test.com", "securePassword456");

        //When
        SignUpUser signUpUser = AuthMapper.toDomain(request);

        //Then
        assertThat(signUpUser).isNotNull();
        assertThat(signUpUser.name()).isEqualTo("Jane");
        assertThat(signUpUser.surname()).isEqualTo("Smith");
        assertThat(signUpUser.email()).isEqualTo("jane.smith@test.com");
        assertThat(signUpUser.password()).isEqualTo("securePassword456");
    }

    @Test
    void should_map_sign_up_request_with_empty_fields() {
        //Given
        SignUpRequest request = new SignUpRequest("", "", "", "");

        //When
        SignUpUser signUpUser = AuthMapper.toDomain(request);

        //Then
        assertThat(signUpUser).isNotNull();
        assertThat(signUpUser.name()).isEmpty();
        assertThat(signUpUser.surname()).isEmpty();
        assertThat(signUpUser.email()).isEmpty();
        assertThat(signUpUser.password()).isEmpty();
    }

    @Test
    void should_map_sign_up_request_with_null_equivalent_fields() {
        //Given
        SignUpRequest request = new SignUpRequest("Name", "Surname", "email@test.com", "password");

        //When
        SignUpUser signUpUser = AuthMapper.toDomain(request);

        //Then
        assertThat(signUpUser).isNotNull();
        assertThat(signUpUser.name()).isEqualTo("Name");
        assertThat(signUpUser.surname()).isEqualTo("Surname");
        assertThat(signUpUser.email()).isEqualTo("email@test.com");
        assertThat(signUpUser.password()).isEqualTo("password");
    }
}

