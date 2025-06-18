package com.oauth2.app.oauth2_authorization_server.service.roles;

import com.oauth2.app.oauth2_authorization_server.dto.request.roles.RolesRequest;
import com.oauth2.app.oauth2_authorization_server.dto.request.roles.RolesUpdateRequest;
import com.oauth2.app.oauth2_authorization_server.dto.response.RoleDetailsResponse;
import com.oauth2.app.oauth2_authorization_server.dto.response.RolesResponse;
import com.oauth2.app.oauth2_authorization_server.models.entity.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IRoleService {
     Page<Roles> findAll(Pageable pageable);
     RoleDetailsResponse findDetails(UUID publicId);
     void delete(UUID publicId);
     Roles save(RolesRequest request);
     Roles update(UUID publicId, RolesUpdateRequest request);
     Roles findByPublicId(UUID publicId);
}
