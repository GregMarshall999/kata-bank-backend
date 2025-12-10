package com.exalt_company.user_domain.spi.stub;

import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import com.exalt_company.user_domain.spi.BankUsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemoryBankUserAccounts implements BankUsers {
    private final Map<UUID, BankUserAccount> bankUserAccounts = new HashMap<>();

    @Override
    public BankUserAccount createAccount(BankUserAccount userAccount) throws BankUserException {
        BankUserAccount found = find(userAccount.getEmail());

        if(found != null) throw new BankUserException("User with this email already exists");

        UUID id = UUID.randomUUID();
        while (bankUserAccounts.containsKey(id)) id = UUID.randomUUID();

        userAccount.setId(id);

        bankUserAccounts.put(id, userAccount);

        return userAccount;
    }

    @Override
    public boolean deleteAccount(UUID userId) {
        if(!bankUserAccounts.containsKey(userId)) return false;

        bankUserAccounts.remove(userId);

        return true;
    }

    @Override
    public BankUserAccount editAccount(UUID userId, BankUserAccount userAccount) throws BankUserException {
        if(!bankUserAccounts.containsKey(userId)) throw new BankUserException("Could not edit non existing user");

        bankUserAccounts.put(userId, userAccount);

        return userAccount;
    }

    @Override
    public BankUserAccount findByEmail(String email) throws BankUserException {
        BankUserAccount found = find(email);

        if(found == null) throw new BankUserException("User not found");

        return found;
    }

    @Override
    public BankUserAccount findById(UUID userId) throws BankUserException {
        if(!bankUserAccounts.containsKey(userId)) throw new BankUserException("User not found");

        return bankUserAccounts.get(userId);
    }

    @Override
    public Page<BankUserAccount> pageAccounts(int page, int size) throws BankUserException {
        if(page < 0 || size < 1) throw new BankUserException("Wrong value for parameters");

        int firstIndex = page * size;
        int lastIndex = firstIndex + size;

        UUID[] uuids = bankUserAccounts.keySet().toArray(new UUID[0]);

        List<BankUserAccount> content = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if(i >= uuids.length) break;

            content.add(bankUserAccounts.get(uuids[i]));
        }

        return new Page<>(content, page, uuids.length / size, uuids.length);
    }

    @Override
    public Page<BankUserAccount> searchAccountsByEmail(String email, int page, int size) throws BankUserException {
        return null; //TODO implement for tests
    }

    private BankUserAccount find(String email) {
        List<BankUserAccount> found = bankUserAccounts.values()
                .stream()
                .filter(userAccount -> email.equals(userAccount.getEmail()))
                .toList();

        if(found.isEmpty()) return null;
        if(found.size() > 1) return null;

        return found.get(0);
    }
}
