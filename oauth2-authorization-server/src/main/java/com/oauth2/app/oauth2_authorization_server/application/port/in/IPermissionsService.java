package com.oauth2.app.oauth2_authorization_server.application.port.in;

import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.roles.ActivePermissionsToRoleRequest;

import java.util.List;
import java.util.UUID;

public interface IPermissionsService {

    List<ActivePermissionsToRoleRequest> getActivePermissionsToRole(UUID publicId);

}
