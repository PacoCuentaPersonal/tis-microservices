package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api;

import com.jcs.jpa.PaginationConstants;
import com.jcs.jpa.SortDirection;
import com.jcs.pagination.PagedResponse;
import com.jcs.response.EnvelopeResponse;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.roles.RolesRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.roles.RolesUpdateRequest;
// Corrected DTO response imports
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response.RoleDetailsResponse; 
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response.RolesResponse;   
import com.oauth2.app.oauth2_authorization_server.domain.model.Role; 
import com.oauth2.app.oauth2_authorization_server.application.port.in.IRoleService;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.util.JpaUtils;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.mapper.RoleMapper; 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus; // Importación añadida
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/roles")
@Tag(name = "Roles", description = "Gestión de los endpoints de roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;
    private final JpaUtils jpaUtils = new JpaUtils(List.of("name", "createdAt", "updatedAt"));
    private final RoleMapper roleMapper;

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
        Page<Role> rolesPage = roleService.findAll(pageable);
        Page<RolesResponse> rolesResponsePage = rolesPage.map(roleMapper::toResponse);
        PagedResponse<RolesResponse> pagedResponse = new PagedResponse<>(
                rolesResponsePage.getContent(),
                rolesResponsePage.getNumber(),
                rolesResponsePage.getSize(),
                rolesResponsePage.getTotalElements(),
                rolesResponsePage.getTotalPages(),
                rolesResponsePage.hasNext(),
                rolesResponsePage.hasPrevious(),
                rolesResponsePage.isFirst(),
                rolesResponsePage.isLast()
        );
        return ResponseEntity.ok(EnvelopeResponse.success(pagedResponse));
    }

    @PostMapping
    @Operation(
            summary = "Crear un nuevo rol"
    )
    public ResponseEntity<EnvelopeResponse<RolesResponse>> createRole(
            @Valid @RequestBody RolesRequest request
    ) {
        Role newRole = roleService.save(request);
        RolesResponse rolesResponse = roleMapper.toResponse(newRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(EnvelopeResponse.success(rolesResponse));
    }

    @GetMapping(value = "/{publicId}/permissions")
    @Operation(
            summary = "Obtener permisos de un rol"
    )
    public ResponseEntity<EnvelopeResponse<RoleDetailsResponse>> getRoleDetails(
            @PathVariable("publicId") UUID publicId
    ) {
        RoleDetailsResponse details = roleService.findDetails(publicId);
        return ResponseEntity.ok(EnvelopeResponse.success(details));
    }

    @PutMapping(value = "/{publicId}")
    @Operation(
            summary = "Actualizar rol"
    )
    public ResponseEntity<EnvelopeResponse<RolesResponse>> updateRole(
            @PathVariable("publicId") UUID publicId,
            @Valid @RequestBody RolesUpdateRequest request
    ) {
        Role updatedRole = roleService.update(publicId, request);
        RolesResponse rolesResponse = roleMapper.toResponse(updatedRole);
        return ResponseEntity.ok(EnvelopeResponse.success(rolesResponse));
    }

    @DeleteMapping(value = "/{publicId}")
    @Operation(
            summary = "Eliminar rol"
    )
    public ResponseEntity<EnvelopeResponse<Void>> deleteRole(
            @PathVariable("publicId") UUID publicId
    ) {
        roleService.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}
