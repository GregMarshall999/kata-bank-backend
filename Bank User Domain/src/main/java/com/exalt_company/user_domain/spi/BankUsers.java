package com.exalt_company.user_domain.spi;

import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.BankUserException;

import java.util.UUID;

/**
 * SPI port for Bank User Account requests
 */
public interface BankUsers {
    /**
     * Creates a user
     * @param userAccount user to create
     * @return new created user
     * @throws BankUserException in case of a persistence failure
     */
    BankUserAccount createAccount(BankUserAccount userAccount) throws BankUserException;

    /**
     * Deletes a user account
     * @param userId the unique identifier of the user to delete
     * @return true if the account was successfully deleted, false otherwise
     */
    boolean deleteAccount(UUID userId);

    /**
     * Edits an existing user account
     * @param userId the unique identifier of the user to edit
     * @param userAccount the updated user account information
     * @return the updated user account
     * @throws BankUserException in case of a persistence failure or if the user is not found
     */
    BankUserAccount editAccount(UUID userId, BankUserAccount userAccount) throws BankUserException;

    /**
     * Finds a user account by email address
     * @param email the email address of the user to find
     * @return the user account associated with the email
     * @throws BankUserException if the user is not found or in case of a persistence failure
     */
    BankUserAccount findByEmail(String email) throws BankUserException;

    /**
     * Finds a user account by its unique identifier
     * @param userId the unique identifier of the user to find
     * @return the user account associated with the identifier
     * @throws BankUserException if the user is not found or in case of a persistence failure
     */
    BankUserAccount findById(UUID userId) throws BankUserException;

    /**
     * Retrieves a paginated list of user accounts
     * @param page the page number to retrieve (0-based)
     * @param size the number of accounts per page
     * @return a page containing user accounts
     */
    Page<BankUserAccount> pageAccounts(int page, int size) throws BankUserException;

    Page<BankUserAccount> searchAccountsByEmail(String email, int page, int size) throws BankUserException;
}
