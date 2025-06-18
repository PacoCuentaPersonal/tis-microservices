package com.jcs.coreservice.service;

import com.jcs.coreservice.model.entity.DistrictEntity;
import com.jcs.coreservice.repository.JpaDistrictRepository;
import com.jcs.coreservice.usecase.IDistrictService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DistrictServiceImpl implements IDistrictService {
    private final JpaDistrictRepository districtRepository;

    public DistrictServiceImpl(JpaDistrictRepository districtRepository) {
        this.districtRepository = districtRepository;
    }
    @Override
    public DistrictEntity getDistrictByPublicId(UUID publicId) {
        return districtRepository.findByPublicIdAndActiveTrue(publicId)
                .orElseThrow(() -> new RuntimeException("District not found"));
    }
}
