package com.exalt_company.kata_bank.service;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.entity.RefreshToken;
import com.exalt_company.kata_bank.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing refresh tokens.
 * Handles creation, validation, expiration checking, and deletion of refresh tokens.
 */
@Service
public class RefreshTokenService {
    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    /**
     * Constructs a new RefreshTokenService with the specified dependencies.
     *
     * @param refreshTokenRepository the repository for refresh token persistence
     * @param jwtService the service for JWT token generation
     */
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    /**
     * Creates a new refresh token for the specified user.
     *
     * @param user the user for whom to create the refresh token
     * @return the created and persisted refresh token
     */
    public RefreshToken createRefreshToken(BankUser user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setToken(jwtService.generateRefreshToken(user));
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Finds a refresh token by its token string.
     *
     * @param token the token string to search for
     * @return an Optional containing the refresh token if found
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifies that a refresh token has not expired.
     * If expired, the token is deleted and an exception is thrown.
     *
     * @param token the refresh token to verify
     * @return the token if it is still valid
     * @throws RuntimeException if the token has expired
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    /**
     * Deletes all refresh tokens associated with a specific user.
     *
     * @param userId the unique identifier of the user
     */
    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUser_Id(userId);
    }

    /**
     * Deletes a specific refresh token.
     *
     * @param refreshToken the refresh token to delete
     */
    @Transactional
    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}

