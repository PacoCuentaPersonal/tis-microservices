package com.jcs.coreservice.repository;

import com.jcs.coreservice.model.entity.DistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaDistrictRepository extends JpaRepository<DistrictEntity, Integer> {
    Optional<DistrictEntity> findByPublicIdAndActiveTrue(UUID publicId);
}
