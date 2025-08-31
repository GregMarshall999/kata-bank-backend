package com.exalt_company.kata_bank_api.dto.fund;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for overdraw operations")
public class OverdrawDto extends BaseFundDto {
    
    @Schema(description = "Maximum overdraw amount allowed for the fund", 
            example = "500.0", 
            minimum = "0.0",
            required = true)
    private double maxOverdraw;

    public double getMaxOverdraw() {
        return maxOverdraw;
    }

    public void setMaxOverdraw(double maxOverdraw) {
        this.maxOverdraw = maxOverdraw;
    }
}
