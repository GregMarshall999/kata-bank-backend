package com.exalt_company.kata_bank_api.dto.fund;

public class OverdrawDto extends BaseFundDto {
    private double maxOverdraw;

    public double getMaxOverdraw() {
        return maxOverdraw;
    }

    public void setMaxOverdraw(double maxOverdraw) {
        this.maxOverdraw = maxOverdraw;
    }
}
