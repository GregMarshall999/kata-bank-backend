package com.exalt_company.kata_bank_api.dto;

import com.exalt_company.kata_bank_api.enums.BankRole;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This represents the data interface of frontend apps calling our API.
 * I prefer to link entities with their ID for frontend sub requests for general use.
 * Later specific cases can have custom mapping of the required fields.
 */
public class BankUserDto extends BaseDto {
    private String name;
    private String surname;
    private String email;
    private BankRole bankRole;

    private long advisorId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BankRole getBankRole() {
        return bankRole;
    }

    public void setBankRole(BankRole bankRole) {
        this.bankRole = bankRole;
    }

    public long getAdvisorId() {
        return advisorId;
    }

    public void setAdvisorId(long advisorId) {
        this.advisorId = advisorId;
    }

    /**
     * At this point I'm just flexing with java's reflexion.
     * There are hundreds of better ways to do this (mapstruct for example)
     * The whole point is to make this fairly adaptable to changes to this DTO
     * @return a fresh copy of a BankUserDto but with the extra password field ready when needed
     * @throws IllegalAccessException
     */
    public PasswordedBankUserDto copy() throws IllegalAccessException {
        PasswordedBankUserDto copy = new PasswordedBankUserDto();
        Map<String, Field> copyFields = Arrays.stream(copy.getClass().getSuperclass().getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, field -> field));

        Field copyField;
        for (Field field : this.getClass().getDeclaredFields()) {
            if(copyFields.containsKey(field.getName())) {
                copyField = copyFields.get(field.getName());

                copyField.set(copy, field.get(this)); //Sonar goes crazy with accessing stuff with encapsulation. I know what I'm doing though.
            }
        }

        copy.setId(getId()); //id is from the superclass so declaredFields won't have it

        return copy;
    }
}
