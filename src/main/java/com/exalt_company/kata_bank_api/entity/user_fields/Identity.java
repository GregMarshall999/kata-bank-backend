package com.exalt_company.kata_bank_api.entity.user_fields;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Identity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

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
}
