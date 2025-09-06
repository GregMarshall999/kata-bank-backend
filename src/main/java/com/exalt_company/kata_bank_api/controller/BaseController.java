package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.dto.PageDto;
import com.exalt_company.kata_bank_api.exception.BaseException;
import com.exalt_company.kata_bank_api.service.IBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import java.util.List;

/**
 * We have the entry points for all CRUD operations for any entity of this project.
 * @param <D> BaseDto inheritors
 * @param <S> Any entity service with CRUD capabilities
 */
@Tag(name = "CRUD Operations", description = "Generic CRUD operations for all entities")
public abstract class BaseController<D extends BaseDto, S extends IBaseService<D>> {
    protected final S service;

    protected BaseController(S service) {
        this.service = service;
    }

    @Operation(summary = "Create new entity", description = "Creates a new entity with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entity created successfully",
                    content = @Content(schema = @Schema(implementation = BaseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Entity already exists")
    })
    @PostMapping
    public ResponseEntity<D> create(@Valid @RequestBody D dto) throws BaseException {
        return service.create(dto);
    }

    @Operation(summary = "Get entity by ID", description = "Retrieves an entity by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entity found successfully",
                    content = @Content(schema = @Schema(implementation = BaseDto.class))),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<D> getById(@Parameter(description = "Unique identifier of the entity", example = "1")
                                         @PathVariable long id) throws BaseException {
        return service.getById(id);
    }

    @Operation(summary = "Get all entities", description = "Retrieves all entities of this type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entities retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BaseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<D>> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Get paginated entities", description = "Retrieves a paginated list of entities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated entities retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageDto.class)))
    })
    @GetMapping("/{page}/{size}")
    public ResponseEntity<PageDto<D>> getPage(
            @Parameter(description = "Page number (0-based)", example = "0") @PathVariable int page,
            @Parameter(description = "Number of items per page", example = "10") @PathVariable int size) {
        return service.getPage(page, size);
    }

    @Operation(summary = "Update entity by ID", description = "Updates an existing entity with new data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entity updated successfully",
                    content = @Content(schema = @Schema(implementation = BaseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<D> update(
            @Parameter(description = "Unique identifier of the entity to update", example = "1") @PathVariable long id,
            @Valid @RequestBody D dto) throws BaseException {
        return service.update(id, dto);
    }

    @Operation(summary = "Delete entity by ID", description = "Deletes an entity by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entity deleted successfully",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteById(@Parameter(description = "Unique identifier of the entity to delete", example = "1") @PathVariable long id) throws BaseException {
        return service.deleteById(id);
    }
}
