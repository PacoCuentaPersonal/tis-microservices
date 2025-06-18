package com.jcs.coreservice.usecase;

import com.jcs.coreservice.model.entity.DocumentTypeEntity;

import java.util.UUID;

public interface IDocumentTypeService {
    DocumentTypeEntity getDocumentTypeByPublicId(UUID publicId);
}
