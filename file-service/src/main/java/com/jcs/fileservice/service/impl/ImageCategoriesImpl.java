package com.jcs.fileservice.service.impl;

import com.jcs.fileservice.models.entity.ImageCategories;
import com.jcs.fileservice.repository.JpaImageCategories;
import com.jcs.fileservice.service.interfaces.IImageCategories;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCategoriesImpl implements IImageCategories {

    private final JpaImageCategories jpaImageCategories;

    @Override
    @Transactional(readOnly = true)
    public Page<ImageCategories> findAllCategories(Pageable pageable) {
        return jpaImageCategories.findAllByActiveTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ImageCategories> findByName(String code, Pageable pageable) {
        return jpaImageCategories.findAllByActiveTrueAndNameContainsIgnoreCase(code, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ImageCategories findByPublicId(UUID publicId) {
        Optional<ImageCategories> byPublicIdAndActiveTrue = jpaImageCategories.findByPublicIdAndActiveTrue(publicId);
        if (byPublicIdAndActiveTrue.isEmpty()) {
            throw new EntityNotFoundException("Image category with public ID '" + publicId + "' not found or is inactive.");
        }
        return byPublicIdAndActiveTrue.get();
    }

    @Override
    @Transactional(readOnly = true)
    public ImageCategories findByName(String code) {
        Optional<ImageCategories> byCodeAndActiveTrue = jpaImageCategories.findByNameAndActiveTrue(code);
        if (byCodeAndActiveTrue.isEmpty()) {
            throw new EntityNotFoundException("Image category with code '" + code + "' not found or is inactive.");
        }
        return byCodeAndActiveTrue.get();
    }

    @Override
    @Transactional
    public ImageCategories create(ImageCategories imageCategories) {

        imageCategories.setActive(true);
        return jpaImageCategories.save(imageCategories);
    }

    @Override
    @Transactional
    public ImageCategories update(UUID publicId, ImageCategories updateData) {
        return jpaImageCategories.findByPublicIdAndActiveTrue(publicId)
                .map(existing -> {
                    if (updateData.getName() != null) {
                        existing.setName(updateData.getName());
                    }
                    if (updateData.getDescription() != null) {
                        existing.setDescription(updateData.getDescription());
                    }
                    return jpaImageCategories.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        "ImageCategory not found with publicId: " + publicId
                ));
    }

    @Override
    @Transactional
    public void delete(UUID publicId) {
        ImageCategories category = jpaImageCategories.findByPublicIdAndActiveTrue(publicId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ImageCategory not found with publicId: " + publicId
                ));

        category.setActive(false);
        jpaImageCategories.save(category);
    }
}