package com.oauth2.app.oauth2_authorization_server.application.port.in;

import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.roles.RolesRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.roles.RolesUpdateRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response.RoleDetailsResponse;
// import com.oauth2.app.oauth2_authorization_server.dto.response.RolesResponse; // RolesResponse might be mapped in the service impl or adapter
// This should ideally refer to the domain model
import com.oauth2.app.oauth2_authorization_server.domain.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// import java.util.List; // Not used
import java.util.UUID;

public interface IRoleService {
     Page<Role> findAll(Pageable pageable);
     RoleDetailsResponse findDetails(UUID publicId);
     void delete(UUID publicId);
     Role save(RolesRequest request);
     Role update(UUID publicId, RolesUpdateRequest request);
     Role findByPublicId(UUID publicId);
}
