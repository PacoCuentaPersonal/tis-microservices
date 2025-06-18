package com.jcs.fileservice.util.mapper;

import com.jcs.fileservice.dto.imageCategories.CreateImageCategoryDto;
import com.jcs.fileservice.dto.imageCategories.ImageCategoryResponseDto;
import com.jcs.fileservice.dto.imageCategories.UpdateImageCategoryDto;
import com.jcs.fileservice.models.entity.ImageCategories;
import org.springframework.stereotype.Component;

@Component
public class ImageCategoryMapper {
    public ImageCategories toEntity(CreateImageCategoryDto dto) {
        ImageCategories entity = new ImageCategories();
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        return entity;
    }

    public ImageCategories toEntity(UpdateImageCategoryDto dto) {
        ImageCategories entity = new ImageCategories();
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        return entity;
    }

    public ImageCategoryResponseDto toResponseDto(ImageCategories entity) {
        return ImageCategoryResponseDto.builder()
                .publicId(entity.getPublicId())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.isActive())
                .createdDate(entity.getCreatedAt())
                .lastModifiedDate(entity.getUpdatedAt())
                .build();
    }
}
