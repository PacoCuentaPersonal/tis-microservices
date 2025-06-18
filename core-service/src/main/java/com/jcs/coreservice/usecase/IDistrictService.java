package com.jcs.coreservice.usecase;

import com.jcs.coreservice.model.entity.DistrictEntity;

import java.util.UUID;

public interface IDistrictService {
    DistrictEntity getDistrictByPublicId(UUID publicId);
}
