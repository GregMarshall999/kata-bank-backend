package com.exalt_company.kata_bank_api.enums;

import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountTypeTest {
    @Test
    void testEnumValues() {
        AccountType[] accountTypes = AccountType.values();

        assertNotNull(accountTypes);
        assertEquals(2, accountTypes.length);
        
        assertTrue(contains(accountTypes, AccountType.FUND));
        assertTrue(contains(accountTypes, AccountType.SAVING));
    }

    @Test
    void testValueOf() {
        assertEquals(AccountType.FUND, AccountType.valueOf("FUND"));
        assertEquals(AccountType.SAVING, AccountType.valueOf("SAVING"));
    }

    @Test
    void testValueOf_InvalidValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> AccountType.valueOf("INVALID_ACCOUNT_TYPE"));
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, AccountType.FUND.ordinal());
        assertEquals(1, AccountType.SAVING.ordinal());
    }

    @Test
    void testEnumName() {
        assertEquals("FUND", AccountType.FUND.name());
        assertEquals("SAVING", AccountType.SAVING.name());
    }

    @Test
    void testEnumToString() {
        assertEquals("FUND", AccountType.FUND.toString());
        assertEquals("SAVING", AccountType.SAVING.toString());
    }

    @Test
    void testEnumHashCode() {
        assertEquals(AccountType.FUND.hashCode(), AccountType.FUND.hashCode());
        assertEquals(AccountType.SAVING.hashCode(), AccountType.SAVING.hashCode());
        
        assertNotEquals(AccountType.FUND.hashCode(), AccountType.SAVING.hashCode());
    }

    @Test
    void testEnumGetDeclaringClass() {
        assertEquals(AccountType.class, AccountType.FUND.getDeclaringClass());
        assertEquals(AccountType.class, AccountType.SAVING.getDeclaringClass());
    }

    @Test
    void testEnumGetClass() {
        assertSame(AccountType.class, AccountType.FUND.getClass());
        assertSame(AccountType.class, AccountType.SAVING.getClass());
    }

    @Test
    void testEnumSwitchStatement() {
        assertEquals("Fund account", getAccountDescription(AccountType.FUND));
        assertEquals("Savings account", getAccountDescription(AccountType.SAVING));
    }

    @Test
    void testEnumInCollections() {
        java.util.Set<AccountType> accountTypeSet = new java.util.HashSet<>();
        java.util.List<AccountType> accountTypeList = new java.util.ArrayList<>();
        
        accountTypeSet.add(AccountType.FUND);
        accountTypeSet.add(AccountType.SAVING);
        
        accountTypeList.add(AccountType.FUND);
        accountTypeList.add(AccountType.SAVING);
        
        assertEquals(2, accountTypeSet.size());
        assertTrue(accountTypeSet.contains(AccountType.FUND));
        assertTrue(accountTypeSet.contains(AccountType.SAVING));
        
        assertEquals(2, accountTypeList.size());
        assertEquals(AccountType.FUND, accountTypeList.get(0));
        assertEquals(AccountType.SAVING, accountTypeList.get(1));
    }

    @Test
    void testEnumInMap() {
        Map<AccountType, String> accountTypeMap = new EnumMap<>(AccountType.class);
        
        accountTypeMap.put(AccountType.FUND, "Current Account");
        accountTypeMap.put(AccountType.SAVING, "Savings Account");
        
        assertEquals(2, accountTypeMap.size());
        assertEquals("Current Account", accountTypeMap.get(AccountType.FUND));
        assertEquals("Savings Account", accountTypeMap.get(AccountType.SAVING));
    }

    @Test
    void testEnumInStream() {
        long fundCount = java.util.Arrays.stream(AccountType.values())
            .filter(type -> type == AccountType.FUND)
            .count();
        
        long savingCount = java.util.Arrays.stream(AccountType.values())
            .filter(type -> type == AccountType.SAVING)
            .count();
        
        assertEquals(1, fundCount);
        assertEquals(1, savingCount);
    }

    @Test
    void testEnumSerialization() {
        String fundString = AccountType.FUND.name();
        String savingString = AccountType.SAVING.name();
        
        assertEquals("FUND", fundString);
        assertEquals("SAVING", savingString);
        
        assertEquals(AccountType.FUND, AccountType.valueOf(fundString));
        assertEquals(AccountType.SAVING, AccountType.valueOf(savingString));
    }

    @Test
    void testEnumPatternMatching() {
        assertTrue(isFundAccount(AccountType.FUND));
        assertFalse(isFundAccount(AccountType.SAVING));
        assertTrue(isSavingAccount(AccountType.SAVING));
        assertFalse(isSavingAccount(AccountType.FUND));
    }

    @Test
    void testEnumInSwitchExpression() {
        assertEquals("Funds", getAccountTypeName(AccountType.FUND));
        assertEquals("Savings", getAccountTypeName(AccountType.SAVING));
    }

    private String getAccountDescription(AccountType accountType) {
        return switch (accountType) {
            case FUND -> "Fund account";
            case SAVING -> "Savings account";
        };
    }

    private boolean isFundAccount(AccountType accountType) {
        return switch (accountType) {
            case FUND -> true;
            case SAVING -> false;
        };
    }

    private boolean isSavingAccount(AccountType accountType) {
        return switch (accountType) {
            case FUND -> false;
            case SAVING -> true;
        };
    }

    private String getAccountTypeName(AccountType accountType) {
        return switch (accountType) {
            case FUND -> "Funds";
            case SAVING -> "Savings";
        };
    }

    private boolean contains(AccountType[] accountTypes, AccountType accountType) {
        for (AccountType type : accountTypes) {
            if (type == accountType) {
                return true;
            }
        }
        return false;
    }
}
