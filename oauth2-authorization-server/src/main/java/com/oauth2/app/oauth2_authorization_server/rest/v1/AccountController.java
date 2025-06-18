package com.oauth2.app.oauth2_authorization_server.rest.v1;

import com.jcs.jpa.PaginationConstants;
import com.jcs.pagination.PagedResponse;
import com.jcs.response.EnvelopeResponse;
import com.oauth2.app.oauth2_authorization_server.dto.request.account.AccountRequest;
import com.oauth2.app.oauth2_authorization_server.dto.request.account.AccountUpdateRequest;
import com.oauth2.app.oauth2_authorization_server.dto.response.AccountResponse;
import com.oauth2.app.oauth2_authorization_server.models.entity.Account;
import com.oauth2.app.oauth2_authorization_server.service.account.IAccountService;
import com.oauth2.app.oauth2_authorization_server.utils.jpa.JpaUtils;
import com.oauth2.app.oauth2_authorization_server.utils.mappers.AccountMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/account")
@Slf4j
@Tag(name = "Gestión de cuentas", description = "Endpoints para la gestión completa de cuentas de usuario")
public class AccountController {

    private JpaUtils jpaUtils = new JpaUtils(List.of("email","username","lastLoginAt","createdAt","updatedAt"));
    private final IAccountService accountService;

    public AccountController( IAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Crear una nueva cuenta de usuario",
            description = """
                    Crear una nueva cuenta de usuario con imagen de perfil y rol específico.
                    **Requisitos:**
                    - La imagen debe ser válida (JPEG, PNG, etc.)
                    - El email debe ser único en el sistema
                    - La contraseña debe cumplir con los requisitos de seguridad
                    - El rol debe existir y estar activo"""
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cuenta creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class),
                            examples = @ExampleObject(
                                    name = "Cuenta creada",
                                    value = """
                        {
                            "success": true,
                            "data": {
                                "publicId": "550e8400-e29b-41d4-a716-446655440000",
                                "email": "usuario@ejemplo.com",
                                "username": "usuario123",
                                "profilePictureUrl": "http://localhost:31249/api/v1/images/personas/abc123",
                                "role": "Usuario",
                                "emailVerified": false,
                                "createdAt": "2025-06-11T10:30:00",
                                "updatedAt": "2025-06-11T10:30:00"
                            }
                        }
                        """
                            )
                    )
            )
    })
    public ResponseEntity<EnvelopeResponse<AccountResponse>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la cuenta a crear (formulario multipart)",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = AccountRequest.class)
                    )
            )
            @Valid @ModelAttribute AccountRequest dto
    ) {
        Account account = accountService.accountCreate(dto);
        EnvelopeResponse<AccountResponse> success = EnvelopeResponse.success(AccountMapper.toResponse(account));
        return ResponseEntity.status(201).body(success);
    }

    @PutMapping(value = "/{publicId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Actualizar cuenta de usuario",
            description = """
                    Actualizar los datos de una cuenta existente.

                    **Comportamiento de los campos:**
                    - **image**: Si se envía, reemplaza la imagen actual. Si no se envía, mantiene la actual.
                    - **email**: Si se envía, actualiza el email (debe ser único). Si es null, mantiene el actual.
                    - **password**: Si se envía, actualiza la contraseña. Si es null, mantiene la actual.
                    - **username**: Si se envía, actualiza el username. Si es null, mantiene el actual.
                    - **publicRoleId**: Si se envía, cambia el rol del usuario. Si es null, mantiene el actual.
                    - **emailVerified**: Si se envía, actualiza el estado de verificación. Si es null, mantiene el actual."""
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta actualizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            )
    })
    public ResponseEntity<EnvelopeResponse<AccountResponse>> update(
            @Parameter(
                    description = "ID público de la cuenta a actualizar",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable UUID publicId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos a actualizar (solo enviar los campos que se desean modificar)",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = AccountUpdateRequest.class)
                    )
            )
            @Valid @ModelAttribute AccountUpdateRequest dto
    ) {
        Account account = accountService.accountUpdate(publicId, dto);
        EnvelopeResponse<AccountResponse> success = EnvelopeResponse.success(AccountMapper.toResponse(account));
        return ResponseEntity.ok(success);
    }

    @GetMapping("/{publicId}")
    @Operation(
            summary = "Obtener cuenta por ID",
            description = "Obtener los detalles de una cuenta específica por su ID público"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            )
    })
    public ResponseEntity<EnvelopeResponse<AccountResponse>> getById(
            @Parameter(
                    description = "ID público de la cuenta",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable UUID publicId
    ) {
        Account account = accountService.findAccountByPublicId(publicId);
        return ResponseEntity.ok(EnvelopeResponse.success(AccountMapper.toResponse(account)));
    }


    @DeleteMapping("/{publicId}")
    @Operation(
            summary = "Eliminar cuenta",
            description = """
                    Desactivar una cuenta de usuario (soft delete).

                    **Nota**: La cuenta no se elimina físicamente, solo se marca como inactiva.
                    Las imágenes asociadas se mantienen por motivos de auditoría."""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada exitosamente"),
    })
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "ID público de la cuenta a eliminar",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable UUID publicId
    ) {
        accountService.accountDelete(publicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    @Operation(
            summary = "Listar cuentas de usuarios con paginación"
    )
    public ResponseEntity<EnvelopeResponse<PagedResponse<AccountResponse>>> getAllWithPagination(
            @Parameter(
                    description = "Número de página (0 por defecto)",
                    example = "0"
            )
            @RequestParam(value = "page", defaultValue = PaginationConstants.DEFAULT_PAGE) int page,

            @Parameter(
                    description = "Número de elementos por página (20 por defecto)",
                    example = "20"
            )
            @RequestParam(value = "size", defaultValue = PaginationConstants.DEFAULT_SIZE) int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = PaginationConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = PaginationConstants.DEFAULT_SORT_DIRECTION) String sortDirection
    ) {
        Pageable pageable = jpaUtils.createPageable(page, size, sortBy, sortDirection);
        Page<AccountResponse> pagedResponse = accountService.fetchAllAccounts(pageable).map(AccountMapper::toResponse);
        PagedResponse<AccountResponse> response = new PagedResponse<>(
                pagedResponse.getContent(),
                pagedResponse.getNumber(),
                pagedResponse.getSize(),
                pagedResponse.getTotalElements(),
                pagedResponse.getTotalPages(),
                pagedResponse.hasNext(),
                pagedResponse.hasPrevious(),
                pagedResponse.isFirst(),
                pagedResponse.isLast()
        );
        return ResponseEntity.ok(EnvelopeResponse.success(response));
    }

    @GetMapping("/validate")
    public ResponseEntity<EnvelopeResponse<Void>> validateAccount(
            @RequestParam(value = "token") String token
    ){
        accountService.verifyAccount(token);
        return ResponseEntity.status(HttpStatus.OK).body(EnvelopeResponse.success(null));
    }


}