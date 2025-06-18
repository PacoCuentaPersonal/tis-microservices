package com.oauth2.app.oauth2_authorization_server.rest.v1;

import com.jcs.jpa.PaginationConstants;
import com.jcs.jpa.SortDirection;
import com.jcs.pagination.PagedResponse;
import com.jcs.response.EnvelopeResponse;
import com.oauth2.app.oauth2_authorization_server.dto.request.roles.RolesRequest;
import com.oauth2.app.oauth2_authorization_server.dto.request.roles.RolesUpdateRequest;
import com.oauth2.app.oauth2_authorization_server.dto.response.RoleDetailsResponse;
import com.oauth2.app.oauth2_authorization_server.dto.response.RolesResponse;
import com.oauth2.app.oauth2_authorization_server.models.entity.Roles;
import com.oauth2.app.oauth2_authorization_server.service.roles.IRoleService;
import com.oauth2.app.oauth2_authorization_server.utils.jpa.JpaUtils;
import com.oauth2.app.oauth2_authorization_server.utils.mappers.RoleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/roles")
@Tag(name = "Roles", description = "Gestión de los endpoints de roles")
public class RoleController {

    private final IRoleService roleService;
    private final JpaUtils jpaUtils = new JpaUtils(List.of("name", "createdAt", "updatedAt"));

    public RoleController(IRoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @Operation(
            summary = "Obtener todos los roles",
            description = "Obtener todos los roles activos con paginación y ordenamiento"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles obtenidos exitosamente")
    })
    public ResponseEntity<EnvelopeResponse<PagedResponse<RolesResponse>>> getRoles(
            @Parameter(description = "Número de página (comienza en 0)", example = "0")
            @RequestParam(value = "page", defaultValue = PaginationConstants.DEFAULT_PAGE) int page,

            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(value = "size", defaultValue = PaginationConstants.DEFAULT_SIZE) int size,

            @Parameter(
                    description = "Campo por el cual ordenar",
                    example = "name",
                    schema = @Schema(allowableValues = {"name", "createdAt", "updatedAt"})
            )
            @RequestParam(value = "sortBy", defaultValue = PaginationConstants.DEFAULT_SORT_BY) String sortBy,

            @Parameter(
                    description = "Dirección del ordenamiento",
                    example = "ASC",
                    schema = @Schema(implementation = SortDirection.class)
            )
            @RequestParam(value = "sortDirection", defaultValue = PaginationConstants.DEFAULT_SORT_DIRECTION) SortDirection sortDirection
    ) {
        Pageable pageable = jpaUtils.createPageable(page, size, sortBy, sortDirection.getValue());
        Page<RolesResponse> rolesResponse = roleService.findAll(pageable).map(RoleMapper::toResponse);
        PagedResponse<RolesResponse> pagedResponse = new PagedResponse<>(
                rolesResponse.getContent(),
                rolesResponse.getNumber(),
                rolesResponse.getSize(),
                rolesResponse.getTotalElements(),
                rolesResponse.getTotalPages(),
                rolesResponse.hasNext(),
                rolesResponse.hasPrevious(),
                rolesResponse.isFirst(),
                rolesResponse.isLast()
        );
        return ResponseEntity.ok(EnvelopeResponse.success(pagedResponse));
    }

    @PostMapping
    @Operation(
            summary = "Crear un nuevo rol",
            description = "Crear un nuevo rol con los permisos especificados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente")
    })
    public ResponseEntity<EnvelopeResponse<RolesResponse>> createRole(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del rol a crear",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RolesRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de creación",
                                    value = """
                            {
                                "name": "Administrador",
                                "description": "Rol con todos los permisos",
                                "publicIdPermissions": [
                                    "550e8400-e29b-41d4-a716-446655440001",
                                    "550e8400-e29b-41d4-a716-446655440002"
                                ]
                            }
                            """
                            )
                    )
            )
            @Valid @RequestBody RolesRequest request
    ) {
        Roles newRole = roleService.save(request);
        RolesResponse rolesResponse = RoleMapper.toResponse(newRole);
        return ResponseEntity.status(201).body(EnvelopeResponse.success(rolesResponse));
    }

    @GetMapping(value = "/{publicId}/permissions")
    @Operation(
            summary = "Obtener permisos de un rol",
            description = "Obtener los permisos asociados a un rol específico con su estado de asignación"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles del rol obtenidos exitosamente")
    })
    public ResponseEntity<EnvelopeResponse<RoleDetailsResponse>> getRoleDetails(
            @Parameter(description = "ID público del rol", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable("publicId") UUID publicId
    ) {
        RoleDetailsResponse details = roleService.findDetails(publicId);
        return ResponseEntity.ok(EnvelopeResponse.success(details));
    }

    @PutMapping(value = "/{publicId}")
    @Operation(
            summary = "Actualizar rol",
            description = """
            Actualizar un rol existente. Comportamiento de los campos:
            
            - **name**: Si se envía, actualiza el nombre. Si es null, mantiene el actual. 
            - **description**: Si se envía, actualiza la descripción. Si es null, mantiene la actual.
            - **publicIdPermissions**: 
              - Si NO se envía el campo (null): Los permisos permanecen sin cambios
              - Si se envía lista vacía []: Se eliminan TODOS los permisos del rol
              - Si se envía con valores: Se actualizan los permisos (agrega nuevos, elimina los que no están)
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente"),
    })
    public ResponseEntity<EnvelopeResponse<RolesResponse>> updateRole(
            @Parameter(description = "ID público del rol a actualizar", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable("publicId") UUID publicId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos a actualizar del rol",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RolesUpdateRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Actualizar solo nombre",
                                            value = """
                                {
                                    "name": "Nuevo nombre del rol"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Eliminar todos los permisos",
                                            value = """
                                {
                                    "publicIdPermissions": []
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Actualizar permisos",
                                            value = """
                                {
                                    "publicIdPermissions": [
                                        "550e8400-e29b-41d4-a716-446655440001",
                                        "550e8400-e29b-41d4-a716-446655440002"
                                    ]
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Actualización completa",
                                            value = """
                                {
                                    "name": "Administrador General",
                                    "description": "Rol con permisos administrativos",
                                    "publicIdPermissions": [
                                        "550e8400-e29b-41d4-a716-446655440001",
                                        "550e8400-e29b-41d4-a716-446655440002",
                                        "550e8400-e29b-41d4-a716-446655440003"
                                    ]
                                }
                                """
                                    )
                            }
                    )
            )
            @Valid @RequestBody RolesUpdateRequest request
    ) {
        Roles updatedRole = roleService.update(publicId, request);
        RolesResponse rolesResponse = RoleMapper.toResponse(updatedRole);
        return ResponseEntity.ok(EnvelopeResponse.success(rolesResponse));
    }

    @DeleteMapping(value = "/{publicId}")
    @Operation(
            summary = "Eliminar rol",
            description = """
            Eliminar (desactivar) un rol existente. 
            
            **Nota**: No se puede eliminar un rol si está asignado a usuarios activos.
            La eliminación es lógica (soft delete), el rol se marca como inactivo.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rol eliminado exitosamente")
    })
    public ResponseEntity<EnvelopeResponse<Void>> deleteRole(
            @Parameter(description = "ID público del rol a eliminar", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable("publicId") UUID publicId
    ) {
        roleService.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}