package com.exalt_company.kata_bank_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(description = "Base DTO class for all entities")
public abstract class BaseDto implements Serializable {
    @Schema(description = "Unique identifier of the entity", example = "1")
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
