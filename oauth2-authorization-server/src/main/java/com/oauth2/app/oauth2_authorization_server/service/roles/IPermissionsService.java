package com.oauth2.app.oauth2_authorization_server.service.roles;

import com.oauth2.app.oauth2_authorization_server.dto.request.roles.ActivePermissionsToRoleRequest;

import java.util.List;
import java.util.UUID;

public interface IPermissionsService {

    List<ActivePermissionsToRoleRequest> getActivePermissionsToRole(UUID publicId);

}
