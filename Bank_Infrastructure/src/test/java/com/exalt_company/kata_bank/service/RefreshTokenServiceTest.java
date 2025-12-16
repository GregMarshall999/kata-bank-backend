package com.exalt_company.kata_bank.service;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.entity.RefreshToken;
import com.exalt_company.kata_bank.repository.RefreshTokenRepository;
import com.exalt_company.user_domain.shared.BankRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private BankUser testUser;
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000L; // 7 days

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
        
        testUser = new BankUser();
        testUser.setId(UUID.randomUUID());
        testUser.setName("John");
        testUser.setSurname("Doe");
        testUser.setEmail("john.doe@test.com");
        testUser.setPassword("password123");
        testUser.setRole(BankRole.CLIENT);
    }

    @Test
    void should_create_refresh_token() {
        //Given
        String tokenValue = "test-refresh-token-123";
        when(jwtService.generateRefreshToken(testUser)).thenReturn(tokenValue);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setId(UUID.randomUUID());
            return token;
        });

        //When
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(testUser);

        //Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.getUser()).isEqualTo(testUser);
        assertThat(refreshToken.getToken()).isEqualTo(tokenValue);
        assertThat(refreshToken.getExpiryDate()).isNotNull();
        
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        
        RefreshToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getUser()).isEqualTo(testUser);
        assertThat(savedToken.getToken()).isEqualTo(tokenValue);
        assertThat(savedToken.getExpiryDate()).isAfter(Instant.now());
    }

    @Test
    void should_set_expiry_date_in_future() {
        //Given
        String tokenValue = "test-refresh-token-123";
        when(jwtService.generateRefreshToken(testUser)).thenReturn(tokenValue);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setId(UUID.randomUUID());
            return token;
        });

        //When
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(testUser);

        //Then
        Instant expiryDate = refreshToken.getExpiryDate();
        assertThat(expiryDate).isAfter(Instant.now());
        
        // Verify expiry is approximately REFRESH_TOKEN_EXPIRATION milliseconds in the future
        long expectedExpiryTime = System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION;
        long actualExpiryTime = expiryDate.toEpochMilli();
        long tolerance = 5000L; // 5 seconds tolerance
        
        assertThat(Math.abs(actualExpiryTime - expectedExpiryTime)).isLessThan(tolerance);
    }

    @Test
    void should_find_refresh_token_by_token_value() {
        //Given
        String tokenValue = "test-refresh-token-123";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setToken(tokenValue);
        refreshToken.setUser(testUser);
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION));
        
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(refreshToken));

        //When
        Optional<RefreshToken> found = refreshTokenService.findByToken(tokenValue);

        //Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(refreshToken);
        assertThat(found.get().getToken()).isEqualTo(tokenValue);
        verify(refreshTokenRepository).findByToken(tokenValue);
    }

    @Test
    void should_return_empty_when_token_not_found() {
        //Given
        String tokenValue = "non-existent-token";
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        //When
        Optional<RefreshToken> found = refreshTokenService.findByToken(tokenValue);

        //Then
        assertThat(found).isNotPresent();
        verify(refreshTokenRepository).findByToken(tokenValue);
    }

    @Test
    void should_verify_non_expired_token() {
        //Given
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setToken("valid-token");
        refreshToken.setUser(testUser);
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION));

        //When
        RefreshToken verified = refreshTokenService.verifyExpiration(refreshToken);

        //Then
        assertThat(verified).isEqualTo(refreshToken);
        verify(refreshTokenRepository, never()).delete(refreshToken);
    }

    @Test
    void should_throw_exception_and_delete_expired_token() {
        //Given
        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setId(UUID.randomUUID());
        expiredToken.setToken("expired-token");
        expiredToken.setUser(testUser);
        expiredToken.setExpiryDate(Instant.now().minusSeconds(3600)); // 1 hour ago

        //When/Then
        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(expiredToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Refresh token was expired. Please make a new signin request");
        
        verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    void should_delete_refresh_tokens_by_user_id() {
        //Given
        UUID userId = testUser.getId();

        //When
        refreshTokenService.deleteByUserId(userId);

        //Then
        verify(refreshTokenRepository).deleteByUser_Id(userId);
    }

    @Test
    void should_delete_refresh_token() {
        //Given
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setToken("token-to-delete");
        refreshToken.setUser(testUser);
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION));

        //When
        refreshTokenService.deleteRefreshToken(refreshToken);

        //Then
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void should_delete_multiple_tokens_for_same_user() {
        //Given
        UUID userId = testUser.getId();

        //When
        refreshTokenService.deleteByUserId(userId);

        //Then
        verify(refreshTokenRepository, times(1)).deleteByUser_Id(userId);
    }

    @Test
    void should_handle_token_that_expires_exactly_now() {
        //Given
        RefreshToken tokenAtExpiration = new RefreshToken();
        tokenAtExpiration.setId(UUID.randomUUID());
        tokenAtExpiration.setToken("token-at-expiration");
        tokenAtExpiration.setUser(testUser);
        tokenAtExpiration.setExpiryDate(Instant.now().minusMillis(1)); // Just expired

        //When/Then
        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(tokenAtExpiration))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Refresh token was expired");
        
        verify(refreshTokenRepository).delete(tokenAtExpiration);
    }
}

