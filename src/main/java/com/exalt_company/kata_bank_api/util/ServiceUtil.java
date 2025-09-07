package com.exalt_company.kata_bank_api.util;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.dto.fund.BaseFundDto;
import com.exalt_company.kata_bank_api.enums.AccountType;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.exception.AuditException;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.exception.SavingException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class ServiceUtil {
    private ServiceUtil() {}

    /**
     * Since most fund operations need to be tight with security, this regroups the checks to avoid repeating them by
     * hand.
     * We check if the token embedded ID is the same as the owner of these funds.
     * If anything goes wrong, they get a slap with the corresponding exception.
     * The handler should provide a simple error message for frontend apps.
     * Admin will have a special privilege.
     * <p>
     * Later on, it would be a good idea to set up an admin override. Since the role is also embedded in the token
     * @param fundDto again to avoid repetition any funding dto must extend this ownerId holder.
     * @param actionErrorMessage custom error message to display if authorization fails
     * @param <F> generic type that extends BaseFundDto
     * @throws FundException if user credentials are invalid, user is not authenticated, or user is not the fund owner
     */
    public static  <F extends BaseFundDto> void checkUserAuthorized(F fundDto, String actionErrorMessage) throws FundException {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(authorities.size() == 1) {
            GrantedAuthority authority = authorities.toArray(new GrantedAuthority[]{})[0];
            BankRole bankRole = BankRole.valueOf(authority.getAuthority());
            if(bankRole.equals(BankRole.ADMIN)) return;
        }

        Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
        if(!(credentials instanceof Long)) throw new FundException("An error has occured with the user credentials", HttpStatus.BAD_REQUEST);
        Long id = (Long) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        if(id == null || id == 0L) throw new FundException(actionErrorMessage, HttpStatus.BAD_REQUEST);
        if(fundDto.getOwnerId() != id) throw new FundException("Attempted to access unauthorized funds", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Simple overload method for similar access checks with savings.
     * @param savingDto the savings DTO containing the owner ID to validate against
     * @param actionErrorMessage custom error message to display if authorization fails
     * @param <F> generic type that extends SavingDto
     * @throws SavingException if user credentials are invalid, user is not authenticated, or user is not the savings owner
     */
    public static  <F extends SavingDto> void checkUserAuthorized(F savingDto, String actionErrorMessage) throws SavingException {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(authorities.size() == 1) {
            GrantedAuthority authority = authorities.toArray(new GrantedAuthority[]{})[0];
            BankRole bankRole = BankRole.valueOf(authority.getAuthority());
            if(bankRole.equals(BankRole.ADMIN)) return;
        }

        Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
        if(!(credentials instanceof Long)) throw new SavingException("An error has occured with the user credentials", HttpStatus.BAD_REQUEST);
        Long id = (Long) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        if(id == null || id == 0L) throw new SavingException(actionErrorMessage, HttpStatus.BAD_REQUEST);
        if(savingDto.getOwnerId() != id) throw new SavingException("Attempted to access unauthorized savings", HttpStatus.UNAUTHORIZED);
    }

    public static void checkUserAuthorized(long ownerId, String actionErrorMessage) throws AuditException {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(authorities.size() == 1) {
            GrantedAuthority authority = authorities.toArray(new GrantedAuthority[]{})[0];
            BankRole bankRole = BankRole.valueOf(authority.getAuthority());
            if(bankRole.equals(BankRole.ADMIN)) return;
        }

        Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
        if(!(credentials instanceof Long)) throw new AuditException("An error has occured with the user credentials", HttpStatus.BAD_REQUEST);
        Long id = (Long) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        if(id == null || id == 0L) throw new AuditException(actionErrorMessage, HttpStatus.BAD_REQUEST);
        if(ownerId != id) throw new AuditException("Attempted to access unauthorized statement", HttpStatus.UNAUTHORIZED);
    }
}
