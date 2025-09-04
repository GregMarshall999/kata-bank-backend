package com.exalt_company.kata_bank_api.repository;

import com.exalt_company.kata_bank_api.entity.AccountAudit;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Saving;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountAuditRepository extends BaseRepository<AccountAudit> {
    
    /**
     * Find all AccountAudit records where the userFund owner is the specified user for the current month.
     * This returns all audit records for operations performed on funds owned by the specified user in the current month.
     * 
     * @param owner the BankUser who owns the funds being audited
     * @param pageable pagination and sorting information
     * @return Page of AccountAudit records for funds owned by the specified user in the current month
     */
    @Query("SELECT a FROM AccountAudit a WHERE a.userFund.owner = :owner AND YEAR(a.createdAt) = YEAR(CURRENT_DATE) AND MONTH(a.createdAt) = MONTH(CURRENT_DATE)")
    Page<AccountAudit> findByUserFundOwnerCurrentMonth(@Param("owner") BankUser owner, Pageable pageable);
    
    /**
     * Find all AccountAudit records where the userSaving owner is the specified user for the current month.
     * This returns all audit records for operations performed on savings owned by the specified user in the current month.
     * 
     * @param owner the BankUser who owns the savings being audited
     * @param pageable pagination and sorting information
     * @return Page of AccountAudit records for savings owned by the specified user in the current month
     */
    @Query("SELECT a FROM AccountAudit a WHERE a.userSaving.owner = :owner AND YEAR(a.createdAt) = YEAR(CURRENT_DATE) AND MONTH(a.createdAt) = MONTH(CURRENT_DATE)")
    Page<AccountAudit> findByUserSavingOwnerCurrentMonth(@Param("owner") BankUser owner, Pageable pageable);

    List<AccountAudit> findAllByUserSaving(Saving userSaving);
}
