package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // Good practice for composite keys
@Embeddable
public class RolePermissionId implements Serializable {
    private Integer permissionId;
    private Integer roleId;
}
