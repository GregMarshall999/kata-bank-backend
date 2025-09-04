package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.BankUserDto;
import com.exalt_company.kata_bank_api.dto.PasswordedBankUserDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.exception.BaseException;
import com.exalt_company.kata_bank_api.mapper.BankUserMapper;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.service.IBankUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * We now have a service with extensive CRUD capabilities through minimal coding.
 * At any point we can override to our needs. Add more specific functionality all while maintaining a safe upscaling.
 */
@Service
public class BankUserService extends BaseService<BankUserDto, BankUser, BankUserMapper, BankUserRepository>
        implements IBankUserService {
    private final PasswordEncoder encoder;

    @Autowired
    public BankUserService(BankUserMapper mapper, BankUserRepository repository, PasswordEncoder encoder) {
        super(mapper, repository, BankUser.class);
        this.encoder = encoder;
    }

    /**
     * Since password is a non-null value and BankUserDtos don't possess the field for security reasons
     * We override the create function just to slot the password
     * A random password generator can later be implemented.
     * We can also slot an email service that would send said password to the corresponding user.
     * @param dto
     * @return
     * @throws BaseException
     */
    @Override
    public ResponseEntity<BankUserDto> create(BankUserDto dto) throws BaseException {
        try {
            PasswordedBankUserDto passwordedDto = dto.copy();

            String temporaryPassword = "temporary123";
            passwordedDto.setPassword(temporaryPassword);

            //I'm gonna do some research on doing this password encoding directly in the mapper.
            BankUser mapped = mapper.toEntity(passwordedDto);
            Credentials credentials = mapped.getCredentials();
            credentials.setPassword(encoder.encode(credentials.getPassword()));
            mapped.setCredentials(credentials);

            BankUser saved = repository.save(mapped);
            PasswordedBankUserDto savedCreated = mapper.toCreatedDto(saved);
            savedCreated.setPassword(temporaryPassword);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCreated);
        } catch (IllegalAccessException e) {
            throw new BaseException("A critical error has occured.");
        }
    }
}
