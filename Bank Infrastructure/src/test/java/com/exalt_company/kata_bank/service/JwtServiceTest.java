package com.exalt_company.kata_bank.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET_KEY = "mySecretKey123456789012345678901234567890123456789012345678901234567890";
    private static final long ACCESS_TOKEN_EXPIRATION = 900000L; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000L; // 7 days

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
    }

    @Test
    void should_generate_access_token() {
        //Given
        UserDetails userDetails = createUserDetails("test@example.com");

        //When
        String token = jwtService.generateAccessToken(userDetails);

        //Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void should_generate_refresh_token() {
        //Given
        UserDetails userDetails = createUserDetails("test@example.com");

        //When
        String token = jwtService.generateRefreshToken(userDetails);

        //Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void should_extract_username_from_token() {
        //Given
        String username = "test@example.com";
        UserDetails userDetails = createUserDetails(username);
        String token = jwtService.generateAccessToken(userDetails);

        //When
        String extractedUsername = jwtService.extractUsername(token);

        //Then
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void should_extract_expiration_from_token() {
        //Given
        UserDetails userDetails = createUserDetails("test@example.com");
        String token = jwtService.generateAccessToken(userDetails);

        //When
        Date expiration = jwtService.extractExpiration(token);

        //Then
        assertThat(expiration).isNotNull();
        assertThat(expiration.getTime()).isGreaterThan(System.currentTimeMillis());
    }

    @Test
    void should_extract_claim_from_token() {
        //Given
        String username = "test@example.com";
        UserDetails userDetails = createUserDetails(username);
        String token = jwtService.generateAccessToken(userDetails);

        //When
        String subject = jwtService.extractClaim(token, Claims::getSubject);

        //Then
        assertThat(subject).isEqualTo(username);
    }

    @Test
    void should_validate_token_for_correct_user() {
        //Given
        String username = "test@example.com";
        UserDetails userDetails = createUserDetails(username);
        String token = jwtService.generateAccessToken(userDetails);

        //When
        Boolean isValid = jwtService.validateToken(token, userDetails);

        //Then
        assertTrue(isValid);
    }

    @Test
    void should_invalidate_token_for_wrong_user() {
        //Given
        UserDetails correctUser = createUserDetails("correct@example.com");
        UserDetails wrongUser = createUserDetails("wrong@example.com");
        String token = jwtService.generateAccessToken(correctUser);

        //When
        Boolean isValid = jwtService.validateToken(token, wrongUser);

        //Then
        assertFalse(isValid);
    }

    @Test
    void should_generate_different_tokens_for_different_users() {
        //Given
        UserDetails user1 = createUserDetails("user1@example.com");
        UserDetails user2 = createUserDetails("user2@example.com");

        //When
        String token1 = jwtService.generateAccessToken(user1);
        String token2 = jwtService.generateAccessToken(user2);

        //Then
        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtService.extractUsername(token1)).isEqualTo("user1@example.com");
        assertThat(jwtService.extractUsername(token2)).isEqualTo("user2@example.com");
    }

    @Test
    void should_generate_access_and_refresh_tokens_differently() {
        //Given
        UserDetails userDetails = createUserDetails("test@example.com");

        //When
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        //Then
        assertThat(accessToken).isNotEqualTo(refreshToken);
        
        Date accessExpiration = jwtService.extractExpiration(accessToken);
        Date refreshExpiration = jwtService.extractExpiration(refreshToken);
        
        assertThat(refreshExpiration.getTime()).isGreaterThan(accessExpiration.getTime());
    }

    @Test
    void should_extract_expiration_date_correctly() {
        //Given
        UserDetails userDetails = createUserDetails("test@example.com");
        String token = jwtService.generateAccessToken(userDetails);
        long expectedExpirationTime = System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION;

        //When
        Date expiration = jwtService.extractExpiration(token);

        //Then
        assertThat(expiration).isNotNull();
        // Allow some tolerance (within 5 seconds) for test execution time
        long tolerance = 5000L;
        assertThat(Math.abs(expiration.getTime() - expectedExpirationTime)).isLessThan(tolerance);
    }

    @Test
    void should_extract_refresh_token_expiration_correctly() {
        //Given
        UserDetails userDetails = createUserDetails("test@example.com");
        String token = jwtService.generateRefreshToken(userDetails);
        long expectedExpirationTime = System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION;

        //When
        Date expiration = jwtService.extractExpiration(token);

        //Then
        assertThat(expiration).isNotNull();
        // Allow some tolerance (within 5 seconds) for test execution time
        long tolerance = 5000L;
        assertThat(Math.abs(expiration.getTime() - expectedExpirationTime)).isLessThan(tolerance);
    }

    @Test
    void should_generate_valid_tokens_that_can_be_parsed() {
        //Given
        UserDetails userDetails = createUserDetails("test@example.com");

        //When
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        //Then
        assertNotNull(jwtService.extractUsername(accessToken));
        assertNotNull(jwtService.extractUsername(refreshToken));
        assertNotNull(jwtService.extractExpiration(accessToken));
        assertNotNull(jwtService.extractExpiration(refreshToken));
    }

    private UserDetails createUserDetails(String username) {
        return User.builder()
                .username(username)
                .password("password")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }
}

