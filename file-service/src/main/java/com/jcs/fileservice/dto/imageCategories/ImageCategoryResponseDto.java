package com.jcs.fileservice.dto.imageCategories;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ImageCategoryResponseDto(
         UUID publicId,
         String name,
         String description,
         boolean active,
         LocalDateTime createdDate,
         LocalDateTime lastModifiedDate
) {
}
