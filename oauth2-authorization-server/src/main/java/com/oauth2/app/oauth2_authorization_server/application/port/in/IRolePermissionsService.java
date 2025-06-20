package com.oauth2.app.oauth2_authorization_server.application.port.in;

// This should ideally refer to the domain model
import com.oauth2.app.oauth2_authorization_server.domain.model.Role;

import java.util.List;
import java.util.UUID;

public interface IRolePermissionsService {
    public void deactivateAllRolePermissions(Role role);
    public void assignPermissionsToRole(List<UUID> permissionPublicIds, Role role);
    public void updateRolePermissions(List<UUID> permissionPublicIds, Role role);
}
