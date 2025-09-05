package com.exalt_company.kata_bank_api.dto;

import com.exalt_company.kata_bank_api.enums.BankRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This represents the data interface of frontend apps calling our API.
 * I prefer to link entities with their ID for frontend sub requests for general use.
 * Later specific cases can have custom mapping of the required fields.
 */
@Schema(description = "Data Transfer Object for bank user information")
public class BankUserDto extends BaseDto {
    @Schema(description = "First name of the bank user", 
            example = "John")
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    
    @Schema(description = "Last name of the bank user", 
            example = "Doe")
    @NotBlank(message = "Surname is required")
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
    private String surname;
    
    @Schema(description = "Email address of the bank user", 
            example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;
    
    @Schema(description = "Role of the bank user in the system", 
            example = "CLIENT", 
            allowableValues = {"ADMIN", "ADVISOR", "CLIENT"})
    @NotNull(message = "Bank role is required")
    private BankRole bankRole;

    @Schema(description = "ID of the advisor assigned to this user (if applicable)", 
            example = "1")
    @Positive(message = "Advisor ID must be a positive number")
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
