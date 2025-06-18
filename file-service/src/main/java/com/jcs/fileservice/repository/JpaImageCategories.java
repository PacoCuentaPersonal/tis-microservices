package com.jcs.fileservice.repository;

import com.jcs.fileservice.models.entity.ImageCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaImageCategories extends JpaRepository<ImageCategories,Long> {
    Page<ImageCategories> findAllByActiveTrue (Pageable pageable);
    Optional<ImageCategories> findByPublicIdAndActiveTrue(UUID publicId);
    Optional<ImageCategories> findByNameAndActiveTrue(String code);
    Page<ImageCategories> findAllByActiveTrueAndNameContainsIgnoreCase(String name, Pageable pageable);
}
