package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.roles;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "Request para actualizar un rol existente")
public record RolesUpdateRequest (
        @Schema(
                description = "Nuevo nombre del rol. Si es null, mantiene el nombre actual",
                example = "Administrador",
                nullable = true
        )
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String name,

        @Schema(
                description = "Nueva descripción del rol. Si es null, mantiene la descripción actual",
                example = "Rol con permisos administrativos completos",
                nullable = true
        )
        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        String description,

        @Schema(
                description = "Lista de IDs públicos de permisos a asignar al rol." +
                              "- Si NO se envía (null): Los permisos no se modifican" +
                              "- Si se envía vacía []: Se eliminan TODOS los permisos" +
                              "- Si se envía con valores: Se actualizan los permisos",
                nullable = true
        )
        List<UUID> publicIdPermissions
) {
    public boolean hasName() {return name != null && !name.isEmpty();}
    public boolean hasDescription() {return description != null && !description.isEmpty();}
    public boolean hasPublicIdPermissions() {return publicIdPermissions != null;}
}
