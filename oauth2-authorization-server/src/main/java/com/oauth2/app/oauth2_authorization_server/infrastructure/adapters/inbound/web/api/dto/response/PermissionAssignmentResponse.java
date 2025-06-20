package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response;

// Updated import for PublicIdVO if it was intended to be used here directly
import com.oauth2.app.oauth2_authorization_server.domain.vo.PublicIdVO;
import java.util.UUID; // Keep if publicId is still UUID in this DTO

public record PermissionAssignmentResponse(
        // Changed to PublicIdVO for consistency with domain-driven DTOs, if applicable.
        // If the service layer already provides PublicIdVO, this is fine.
        // If it provides UUID, then the controller/mapper would convert.
        // For now, matching the previous use in PermissionService which created this DTO.
        PublicIdVO publicId, 
        String code,
        String name,
        boolean assigned
) {
}
