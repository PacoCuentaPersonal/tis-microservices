package com.oauth2.app.oauth2_authorization_server.dto.request.roles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class ActivePermissionsToRoleRequest {
    private UUID publicId;
    private String code;
    private String name;
    private boolean assigned;
}
