package com.exalt_company.kata_bank_api.util;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.dto.fund.BaseFundDto;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.exception.SavingException;
import com.exalt_company.kata_bank_api.security.JwtService;

public class ServiceUtil {
    /**
     * Since most fund operations need to be tight with security, this regroups the checks to avoid repeating them by
     * hand.
     * We check if the token embedded ID is the same as the owner of these funds.
     * If anything goes wrong, they get a slap with the corresponding exception.
     * The handler should provide a simple error message for frontend apps.
     * <p>
     * Later on, it would be a good idea to set up an admin override. Since the role is also embedded in the token
     * @param token we find the requesting user ID here and use jwtService to extract it.
     * @param fundDto again to avoid repetition any funding dto must extend this ownerId holder.
     * @param actionErrorMessage custom error messages
     * @param <F>
     * @throws FundException
     */
    public static  <F extends BaseFundDto> void checkUserAuthorized(
            String token, F fundDto, JwtService jwtService, String actionErrorMessage) throws FundException {
        Long id = jwtService.extractId(token);
        if(id == null || id == 0L) throw new FundException(actionErrorMessage);
        if(fundDto.getOwnerId() != id) throw new FundException("Attempted to access unauthorized funds");
    }

    /**
     * Simple overload method for similar access checks with savings.
     * @param token
     * @param savingDto
     * @param jwtService
     * @param actionErrorMessage
     * @param <F>
     * @throws SavingException
     */
    public static  <F extends SavingDto> void checkUserAuthorized(
            String token, F savingDto, JwtService jwtService, String actionErrorMessage) throws SavingException {
        Long id = jwtService.extractId(token);
        if(id == null || id == 0L) throw new SavingException(actionErrorMessage);
        if(savingDto.getOwnerId() != id) throw new SavingException("Attempted to access unauthorized savings");
    }
}
