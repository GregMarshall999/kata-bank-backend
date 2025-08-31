package com.exalt_company.kata_bank_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated response wrapper for entity lists")
public class PageDto<D extends BaseDto> {
    @Schema(description = "List of entities in the current page")
    private List<D> content;
    
    @Schema(description = "Current page number (0-based)", example = "0")
    private int number;
    
    @Schema(description = "Number of items per page", example = "10")
    private int size;
    
    @Schema(description = "Total number of pages", example = "5")
    private int totalPages;
    
    @Schema(description = "Total number of elements across all pages", example = "50")
    private long totalElements;
    
    @Schema(description = "Number of elements in the current page", example = "10")
    private int numberOfElements;

    public List<D> getContent() {
        return content;
    }

    public void setContent(List<D> content) {
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}
