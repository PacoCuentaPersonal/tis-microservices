package com.jcs.coreservice.repository;

import com.jcs.coreservice.model.entity.DocumentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface JpaDocumentTypeRepository extends JpaRepository<DocumentTypeEntity,Integer> {
    Optional<DocumentTypeEntity> findByPublicIdAndActiveTrue(UUID publicId);
}
