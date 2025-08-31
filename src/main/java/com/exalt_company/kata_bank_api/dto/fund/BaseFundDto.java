package com.exalt_company.kata_bank_api.dto.fund;

import com.exalt_company.kata_bank_api.dto.BaseDto;

public abstract class BaseFundDto extends BaseDto {
    private long ownerId;

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
