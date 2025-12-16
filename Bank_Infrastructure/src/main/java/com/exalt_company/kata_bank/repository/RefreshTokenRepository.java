package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends BaseRepository<RefreshToken> {
    Optional<RefreshToken> findByToken(String token);
    
    void deleteByUser_Id(java.util.UUID userId);
}

