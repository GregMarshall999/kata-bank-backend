package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.fund.BaseFundDto;
import com.exalt_company.kata_bank_api.dto.fund.FundDto;
import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.mapper.FundMapper;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.security.JwtService;
import com.exalt_company.kata_bank_api.service.IFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FundService extends BaseService<FundDto, Fund, FundMapper, FundRepository> implements IFundService {
    private final JwtService jwtService;

    @Autowired
    public FundService(FundMapper mapper, FundRepository repository, JwtService jwtService) {
        super(mapper, repository, Fund.class);
        this.jwtService = jwtService;
    }

    /**
     * This handles depositing.
     * When first deposits are made and when an existing balance needs updating.
     * We check if the user requesting the deposit is the funds' owner.
     * Later this can be expanded to admin authority.
     * @param dto
     * @return a deposit status
     * @throws FundException
     */
    @Override
    public ResponseEntity<Banking> deposit(FundOpDto dto, String token) throws FundException {
        checkUserAuthorized(token, dto, "No authorization for deposits");

        if(dto.getId() == 0L) {
            repository.save(mapper.toEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(Banking.DEPOSITED);
        }

        Fund found = repository.findById(dto.getId()).orElseThrow(
                () -> new FundException("No balance to add funds"));

        found.setBalance(found.getBalance() + dto.getBalance());
        repository.save(found);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.DEPOSITED);
    }

    /**
     * We now have the ability to overdraw the balance.
     * As long as the result does not exceed the authorized overdraw value.
     * These operations are available only if the fund has the overdraw enabled feature active.
     * @param dto
     * @param token
     * @return
     * @throws FundException
     */
    @Override
    public ResponseEntity<Banking> withdraw(FundOpDto dto, String token) throws FundException {
        checkUserAuthorized(token, dto, "No authorization for withdrawals");

        if(dto.getId() == 0L)
            throw new FundException("No balance to withdraw from");

        Fund found = repository.findById(dto.getId()).orElseThrow(
                () -> new FundException("No balance to withdraw from"));

        if(!found.canOverdraw() && found.getBalance() - dto.getBalance() < 0)
            throw new FundException("Attempting to withdraw more than available");

        if(found.canOverdraw() && found.getBalance() - dto.getBalance() < -found.getMaxOverdraw())
            throw new FundException("Attempting to withdraw more than allowed");

        found.setBalance(found.getBalance() - dto.getBalance());
        repository.save(found);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.WITHDREW);
    }

    /**
     * We can simulate a contact system with the advisor in order to activate the overdraw feature.
     * For now we assume the request is always authorized
     * @param overdrawDto
     * @return
     */
    @Override
    public ResponseEntity<Banking> requestOverdrawCapabilities(
            OverdrawDto overdrawDto, String token) throws FundException {
        if(overdrawDto.getId() == 0L)
            throw new FundException("No funds to overdraw");

        checkUserAuthorized(token, overdrawDto, "No authorization for overdraws");

        Fund found = repository.findById(overdrawDto.getId())
                .orElseThrow(() -> new FundException("No funds to overdraw"));

        found.setCanOverdraw(true);
        found.setMaxOverdraw(overdrawDto.getMaxOverdraw());

        repository.save(found);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.AUTHORIZED);
    }

    /**
     * Before canceling the overdraw operations, the user's balance must not be negative.
     * @param overdrawDto
     * @param token
     * @return
     * @throws FundException
     */
    @Override
    public ResponseEntity<Banking> cancelOverdrawCapabilities(
            OverdrawDto overdrawDto, String token) throws FundException {
        if(overdrawDto.getId() == 0L)
            throw new FundException("No funds overdrawn to cancel");

        checkUserAuthorized(token, overdrawDto, "No authorization for overdraw cancellation");

        Fund found = repository.findById(overdrawDto.getId())
                .orElseThrow(() -> new FundException("No funds overdrawn to cancel"));

        if(found.getBalance() < 0)
            throw new FundException("Cannot cancel overdraw when balance is negative");

        found.setMaxOverdraw(0D);
        found.setCanOverdraw(false);

        repository.save(found);

        return ResponseEntity.status(HttpStatus.OK).body(Banking.COMPLETED);
    }

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
    private <F extends BaseFundDto> void checkUserAuthorized(
            String token, F fundDto, String actionErrorMessage) throws FundException {
        Long id = jwtService.extractId(token);
        if(id == null || id == 0L) throw new FundException(actionErrorMessage);
        if(fundDto.getOwnerId() != id) throw new FundException("Attempted to access unauthorized funds");
    }
}
