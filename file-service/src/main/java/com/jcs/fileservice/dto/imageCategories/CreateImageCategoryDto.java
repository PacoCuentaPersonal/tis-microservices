package com.jcs.fileservice.dto.imageCategories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateImageCategoryDto(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 50, message = "Code must be between 3 and 50 characters")
        String name,
        @NotBlank(message = "Description is required")
        @Size(min = 3, max = 200, message = "Description must be between 3 and 200 characters")
        String description
) {
}
