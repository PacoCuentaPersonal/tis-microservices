package com.jcs.fileservice.controller.v1;

import com.jcs.fileservice.dto.imageCategories.CreateImageCategoryDto;
import com.jcs.fileservice.dto.imageCategories.ImageCategoryResponseDto;
import com.jcs.fileservice.dto.imageCategories.UpdateImageCategoryDto;
import com.jcs.fileservice.models.entity.ImageCategories;
import com.jcs.fileservice.service.interfaces.IImageCategories;
import com.jcs.fileservice.util.jpa.JpaUtils;
import com.jcs.fileservice.util.mapper.ImageCategoryMapper;
import com.jcs.jpa.PaginationConstants;
import com.jcs.pagination.PagedResponse;
import com.jcs.response.EnvelopeResponse;
import com.jcs.response.ResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/image-categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Image Categories", description = "Image categories management endpoints")
public class ImageCategoriesController {

    private final IImageCategories imageCategoriesService;
    private final ImageCategoryMapper mapper;
    private final JpaUtils sorter= new JpaUtils(List.of("name", "code", "description", "createdAt", "updatedAt"));


    @GetMapping
    @Operation(summary = "Get all categories", description = "Get all active image categories with pagination")
    public ResponseEntity<EnvelopeResponse<PagedResponse<ImageCategoryResponseDto>>> getAllCategories(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE) Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_SIZE)  Integer size,
            @Parameter(description = "Sort field(s). Format: field1,field2")
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_SORT_BY) String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_SORT_DIRECTION) String sortDirection) {

        Pageable pageable = this.sorter.createPageable(page, size, sortBy, sortDirection);
        Page<ImageCategoryResponseDto> categories = imageCategoriesService
                .findAllCategories(pageable)
                .map(mapper::toResponseDto);

        PagedResponse<ImageCategoryResponseDto> pagedResponse = new PagedResponse<>(
                categories.getContent(),
                categories.getNumber(),
                categories.getSize(),
                categories.getTotalElements(),
                categories.getTotalPages(),
                categories.hasNext(),
                categories.hasPrevious(),
                categories.isFirst(),
                categories.isLast()
        );

        return ResponseEntity.ok(EnvelopeResponse.success(pagedResponse));
    }

    @GetMapping("/search")
    @Operation(summary = "Search categories by name", description = "Search active categories by code with pagination")
    public ResponseEntity<EnvelopeResponse<PagedResponse<ImageCategoryResponseDto>>> searchByName(
            @Parameter(description = "Code to search", required = true)
            @RequestParam String name,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_PAGE) @Min(0) Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_SIZE) @Min(1) @Max(100) Integer size,
            @Parameter(description = "Sort field(s). Format: field1,field2")
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_SORT_BY) String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_SORT_DIRECTION) String sortDirection) {

        Pageable pageable = this.sorter.createPageable(page, size, sortBy, sortDirection);

        Page<ImageCategoryResponseDto> categories = imageCategoriesService
                .findByName(name, pageable)
                .map(mapper::toResponseDto);

        PagedResponse<ImageCategoryResponseDto> pagedResponse = new PagedResponse<>(
                categories.getContent(),
                categories.getNumber(),
                categories.getSize(),
                categories.getTotalElements(),
                categories.getTotalPages(),
                categories.hasNext(),
                categories.hasPrevious(),
                categories.isFirst(),
                categories.isLast()
        );

        return ResponseEntity.ok(EnvelopeResponse.success(pagedResponse));
    }

    @GetMapping("/{publicId}")
    @Operation(summary = "Get category by ID", description = "Get a specific category by its public ID")
    @ApiResponse(responseCode = "200", description = "Category found")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<EnvelopeResponse<ImageCategoryResponseDto>> getByPublicId(@PathVariable UUID publicId) {
        ImageCategories byPublicId = imageCategoriesService.findByPublicId(publicId);
        ImageCategoryResponseDto response = mapper.toResponseDto(byPublicId);
        return ResponseEntity.ok(EnvelopeResponse.success(response));
    }


    @PostMapping
    @Operation(summary = "Create category", description = "Create a new image category")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public ResponseEntity<EnvelopeResponse<ImageCategoryResponseDto>> create(
            @Valid @RequestBody CreateImageCategoryDto dto) {

        log.info("Creating new image category with code: {}", dto.name());

        var entity = mapper.toEntity(dto);
        var created = imageCategoriesService.create(entity);
        var response = mapper.toResponseDto(created);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(EnvelopeResponse.success(response));
    }

    @PutMapping("/{publicId}")
    @Operation(summary = "Update category", description = "Update an existing image category")
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public ResponseEntity<EnvelopeResponse<ImageCategoryResponseDto>> update(
            @PathVariable UUID publicId,
            @Valid @RequestBody UpdateImageCategoryDto dto) {

        if (!dto.hasAnyField()) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Updating image category: {}", publicId);

        var entity = mapper.toEntity(dto);
        ImageCategories update = imageCategoriesService.update(publicId, entity);
        ImageCategoryResponseDto response = mapper.toResponseDto(update);
        return ResponseEntity.ok(EnvelopeResponse.success(response));
    }

    @DeleteMapping("/{publicId}")
    @Operation(summary = "Delete category", description = "Soft delete an image category")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<EnvelopeResponse<Void>> delete(@PathVariable UUID publicId) {
        log.info("Deleting image category: {}", publicId);
        imageCategoriesService.delete(publicId);
        return ResponseEntity.ok(EnvelopeResponse.<Void>builder()
                .status(ResponseStatus.SUCCESS)
                .timestamp(Instant.now().toString())
                .build());
    }



}