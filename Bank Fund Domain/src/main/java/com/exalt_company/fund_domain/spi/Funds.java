package com.exalt_company.fund_domain.spi;

import com.exalt_company.fund_domain.domain.Fund;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;

import java.util.UUID;

/**
 * Service Provider Interface for fund persistence operations.
 * This interface defines the contract for storing and retrieving fund data.
 */
public interface Funds {
    /**
     * Creates a new fund for the specified owner.
     *
     * @param ownerId the unique identifier of the fund owner
     * @return the newly created fund
     * @throws FundException if the fund creation fails
     */
    Fund createFund(UUID ownerId) throws FundException;

    /**
     * Retrieves a fund by its owner's unique identifier.
     *
     * @param ownerId the unique identifier of the fund owner
     * @return the fund associated with the owner
     * @throws FundException if the fund is not found
     */
    Fund getByOwnerId(UUID ownerId) throws FundException;

    /**
     * Updates an existing fund with new data.
     *
     * @param fundId the unique identifier of the fund to update
     * @param fundToUpdate the fund object containing the updated data
     * @return the status of the update operation
     * @throws FundException if the update operation fails
     */
    FundStatus updateFund(UUID fundId, Fund fundToUpdate) throws FundException;
}
