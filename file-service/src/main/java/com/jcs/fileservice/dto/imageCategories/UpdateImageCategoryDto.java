package com.jcs.fileservice.dto.imageCategories;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateImageCategoryDto(
        @Size(min = 3, max = 50, message = "Code must be between 3 and 50 characters")
        @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "Code must start with uppercase letter and contain only uppercase letters, numbers and underscores")
        String name,
        @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
        String description
) {
    public boolean hasName() {
        return name != null && !name.trim().isEmpty();
    }

    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }

    public boolean hasAnyField() {
        return hasName() || hasDescription();
    }
}
