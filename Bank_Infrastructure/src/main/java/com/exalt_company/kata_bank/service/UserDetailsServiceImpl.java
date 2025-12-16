package com.exalt_company.kata_bank.service;

import com.exalt_company.kata_bank.repository.BankUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring Security's UserDetailsService.
 * Loads user details by email address for authentication purposes.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final BankUserRepository userRepository;

    /**
     * Constructs a new UserDetailsServiceImpl with the specified user repository.
     *
     * @param userRepository the repository for user operations
     */
    public UserDetailsServiceImpl(BankUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details by email address.
     *
     * @param email the email address (username) of the user to load
     * @return the UserDetails for the specified user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}

