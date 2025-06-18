package com.jcs.coreservice.service;

import com.jcs.coreservice.model.entity.DocumentTypeEntity;
import com.jcs.coreservice.repository.JpaDocumentTypeRepository;
import com.jcs.coreservice.usecase.IDocumentTypeService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DocumentTypeServiceImpl implements IDocumentTypeService {
    private final JpaDocumentTypeRepository documentTypeRepository;

    public DocumentTypeServiceImpl(JpaDocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    @Override
    public DocumentTypeEntity getDocumentTypeByPublicId(UUID publicId) {
        return documentTypeRepository.findByPublicIdAndActiveTrue(publicId).orElseThrow(
                () -> new RuntimeException("Document type not found")
        );
    }
}
