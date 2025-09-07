package com.exalt_company.kata_bank_api.dto.fund;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Data transfer object for overdraw operations")
public class OverdrawDto extends BaseFundDto {
    
    @Schema(description = "Maximum overdraw amount allowed for the fund", 
            example = "500.0", 
            minimum = "0.0")
    @NotNull(message = "Maximum overdraw amount is required")
    @PositiveOrZero(message = "Maximum overdraw amount must be positive")
    private double maxOverdraw;

    public double getMaxOverdraw() {
        return maxOverdraw;
    }

    public void setMaxOverdraw(double maxOverdraw) {
        this.maxOverdraw = maxOverdraw;
    }
}
