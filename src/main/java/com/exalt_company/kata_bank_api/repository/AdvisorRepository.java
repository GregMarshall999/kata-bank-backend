package com.exalt_company.kata_bank_api.repository;

import com.exalt_company.kata_bank_api.entity.Advisor;
import org.springframework.stereotype.Repository;

/**
 * The final step before the database.
 * The repository will have all ORM functions to call database events.
 * The marker helps for the injection in the service.
 */
@Repository
public interface AdvisorRepository extends BaseRepository<Advisor> {
}
