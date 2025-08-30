package com.exalt_company.kata_bank_api.dto;

import java.io.Serializable;

public abstract class BaseDto implements Serializable {
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
