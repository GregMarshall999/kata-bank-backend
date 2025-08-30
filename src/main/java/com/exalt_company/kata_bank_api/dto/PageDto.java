package com.exalt_company.kata_bank_api.dto;

import java.util.List;

public class PageDto<D extends BaseDto> {
    private List<D> content;
}
