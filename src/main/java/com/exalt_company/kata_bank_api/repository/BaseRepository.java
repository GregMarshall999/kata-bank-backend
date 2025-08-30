package com.exalt_company.kata_bank_api.repository;

import com.exalt_company.kata_bank_api.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Database entry point via JPA ORM.
 * Once again we enclose the capabilities around our BaseEntity.
 * @param <E>
 */
@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity> extends JpaRepository<E, Long> {
}
