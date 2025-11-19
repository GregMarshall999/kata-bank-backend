package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.entity.RefreshToken;
import com.exalt_company.user_domain.shared.BankRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RefreshTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private BankUserRepository bankUserRepository;

    private BankUser user;
    private String tokenValue;

    @BeforeEach
    void setUp() {
        user = new BankUser();
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john.doe@test.com");
        user.setPassword("password123");
        user.setRole(BankRole.CLIENT);
        user = entityManager.persistAndFlush(user);

        tokenValue = "test-refresh-token-123";
    }

    @Test
    void should_save_refresh_token() {
        //Given
        RefreshToken token = new RefreshToken();
        token.setToken(tokenValue);
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        token.setUser(user);

        //When
        RefreshToken saved = refreshTokenRepository.save(token);

        //Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getToken()).isEqualTo(tokenValue);
        assertThat(saved.getUser()).isEqualTo(user);
    }

    @Test
    void should_find_token_by_id() {
        //Given
        RefreshToken token = new RefreshToken();
        token.setToken(tokenValue);
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        token.setUser(user);
        RefreshToken saved = entityManager.persistAndFlush(token);

        //When
        Optional<RefreshToken> found = refreshTokenRepository.findById(saved.getId());

        //Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
        assertThat(found.get().getToken()).isEqualTo(tokenValue);
    }

    @Test
    void should_not_find_token_by_non_existent_id() {
        //When
        Optional<RefreshToken> found = refreshTokenRepository.findById(UUID.randomUUID());

        //Then
        assertThat(found).isNotPresent();
    }

    @Test
    void should_find_token_by_token_value() {
        //Given
        RefreshToken token = new RefreshToken();
        token.setToken(tokenValue);
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        token.setUser(user);
        RefreshToken saved = entityManager.persistAndFlush(token);

        //When
        Optional<RefreshToken> found = refreshTokenRepository.findByToken(tokenValue);

        //Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
        assertThat(found.get().getToken()).isEqualTo(tokenValue);
        assertThat(found.get().getUser()).isEqualTo(user);
    }

    @Test
    void should_not_find_token_by_non_existent_token_value() {
        //When
        Optional<RefreshToken> found = refreshTokenRepository.findByToken("non-existent-token");

        //Then
        assertThat(found).isNotPresent();
    }

    @Test
    void should_find_token_by_token_value_case_sensitive() {
        //Given
        RefreshToken token = new RefreshToken();
        token.setToken(tokenValue);
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        token.setUser(user);
        entityManager.persistAndFlush(token);

        //When
        Optional<RefreshToken> found = refreshTokenRepository.findByToken(tokenValue.toUpperCase());

        //Then
        assertThat(found).isNotPresent();
    }

    @Test
    void should_delete_tokens_by_user_id() {
        //Given
        RefreshToken token1 = new RefreshToken();
        token1.setToken("token-1");
        token1.setExpiryDate(Instant.now().plusSeconds(3600));
        token1.setUser(user);
        entityManager.persistAndFlush(token1);

        RefreshToken token2 = new RefreshToken();
        token2.setToken("token-2");
        token2.setExpiryDate(Instant.now().plusSeconds(3600));
        token2.setUser(user);
        entityManager.persistAndFlush(token2);

        BankUser user2 = new BankUser();
        user2.setName("Jane");
        user2.setSurname("Smith");
        user2.setEmail("jane.smith@test.com");
        user2.setPassword("password456");
        user2.setRole(BankRole.CLIENT);
        user2 = entityManager.persistAndFlush(user2);

        RefreshToken token3 = new RefreshToken();
        token3.setToken("token-3");
        token3.setExpiryDate(Instant.now().plusSeconds(3600));
        token3.setUser(user2);
        entityManager.persistAndFlush(token3);

        //When
        refreshTokenRepository.deleteByUser_Id(user.getId());
        entityManager.flush();

        //Then
        Optional<RefreshToken> found1 = refreshTokenRepository.findByToken("token-1");
        Optional<RefreshToken> found2 = refreshTokenRepository.findByToken("token-2");
        Optional<RefreshToken> found3 = refreshTokenRepository.findByToken("token-3");

        assertThat(found1).isNotPresent();
        assertThat(found2).isNotPresent();
        assertThat(found3).isPresent(); // Token for user2 should still exist
    }

    @Test
    void should_find_all_tokens() {
        //Given
        RefreshToken token1 = new RefreshToken();
        token1.setToken("token-1");
        token1.setExpiryDate(Instant.now().plusSeconds(3600));
        token1.setUser(user);
        entityManager.persistAndFlush(token1);

        RefreshToken token2 = new RefreshToken();
        token2.setToken("token-2");
        token2.setExpiryDate(Instant.now().plusSeconds(3600));
        token2.setUser(user);
        entityManager.persistAndFlush(token2);

        //When
        var allTokens = refreshTokenRepository.findAll();

        //Then
        assertThat(allTokens).hasSize(2);
    }

    @Test
    void should_delete_token() {
        //Given
        RefreshToken token = new RefreshToken();
        token.setToken(tokenValue);
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        token.setUser(user);
        RefreshToken saved = entityManager.persistAndFlush(token);

        //When
        refreshTokenRepository.delete(saved);
        entityManager.flush();

        //Then
        Optional<RefreshToken> found = refreshTokenRepository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }

    @Test
    void should_update_token() {
        //Given
        RefreshToken token = new RefreshToken();
        token.setToken(tokenValue);
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        token.setUser(user);
        RefreshToken saved = entityManager.persistAndFlush(token);

        //When
        String newTokenValue = "updated-token-456";
        Instant newExpiryDate = Instant.now().plusSeconds(7200);
        saved.setToken(newTokenValue);
        saved.setExpiryDate(newExpiryDate);
        RefreshToken updated = refreshTokenRepository.save(saved);
        entityManager.flush();

        //Then
        Optional<RefreshToken> found = refreshTokenRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getToken()).isEqualTo(newTokenValue);
    }

    @Test
    void should_delete_by_non_existent_user_id_without_error() {
        //When
        refreshTokenRepository.deleteByUser_Id(UUID.randomUUID());
        entityManager.flush();

        //Then - should not throw exception
        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }
}

