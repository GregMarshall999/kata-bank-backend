package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

/**
 * Base repository interface for all entity repositories.
 * Provides common JPA repository functionality with UUID as the primary key type.
 *
 * @param <E> the entity type that extends BaseEntity
 */
@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity> extends JpaRepository<E, UUID> {
}
