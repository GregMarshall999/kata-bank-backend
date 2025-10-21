package com.exalt_company.user_domain.api;

import com.exalt_company.user_domain.api.resource.AdminResponse;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.Page;

import java.util.UUID;

/**
 * Administration CRUD interface for managing bank user accounts.
 * <p>
 * This interface provides administrative operations for creating, reading, updating,
 * and deleting bank user accounts. All operations return an {@link AdminResponse}
 * to indicate the success or failure state of the operation.
 * </p>
 */
public interface AccountManagement {
    
    /**
     * Creates a new custom bank user account.
     *
     * @param user the bank user account to create
     * @return an {@link AdminResponse} containing the created account if successful,
     *         or an error state if the operation failed
     */
    AdminResponse<BankUserAccount> createCustomAccount(BankUserAccount user);
    
    /**
     * Deletes an existing bank user account.
     *
     * @param userId the unique identifier of the user account to delete
     * @return an {@link AdminResponse} containing true if the deletion was successful,
     *         false otherwise, or an error state if the operation failed
     */
    AdminResponse<Boolean> deleteAccount(UUID userId);
    
    /**
     * Updates an existing bank user account with new information.
     *
     * @param userId the unique identifier of the user account to update
     * @param user the updated bank user account information
     * @return an {@link AdminResponse} containing the updated account if successful,
     *         or an error state if the operation failed
     */
    AdminResponse<BankUserAccount> editAccount(UUID userId, BankUserAccount user);
    
    /**
     * Retrieves a paginated list of all bank user accounts.
     *
     * @param page the page number to retrieve (zero-based)
     * @param size the number of accounts per page
     * @return an {@link AdminResponse} containing a {@link Page} of bank user accounts
     *         if successful, or an error state if the operation failed
     */
    AdminResponse<Page<BankUserAccount>> listAccounts(int page, int size);
    
    /**
     * Retrieves a specific bank user account by its unique identifier.
     *
     * @param userId the unique identifier of the user account to retrieve
     * @return an {@link AdminResponse} containing the requested account if found,
     *         or an error state if the account does not exist or the operation failed
     */
    AdminResponse<BankUserAccount> getAccount(UUID userId);
}
