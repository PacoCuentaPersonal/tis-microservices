package com.oauth2.app.oauth2_authorization_server.service.roles;

import com.oauth2.app.oauth2_authorization_server.models.entity.Roles;

import java.util.List;
import java.util.UUID;

public interface IRolePermissionsService {
    public void deactivateAllRolePermissions(Roles role);
    public void assignPermissionsToRole(List<UUID> permissionPublicIds, Roles role);
    public void updateRolePermissions(List<UUID> permissionPublicIds, Roles role);
}
