package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api;

import com.jcs.jpa.PaginationConstants;
import com.jcs.pagination.PagedResponse;
import com.jcs.response.EnvelopeResponse;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.account.AccountRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.account.AccountUpdateRequest;
// Corrected DTO response import
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response.AccountResponse; 
import com.oauth2.app.oauth2_authorization_server.domain.model.Account;
import com.oauth2.app.oauth2_authorization_server.application.port.in.IAccountService;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.util.JpaUtils;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.mapper.AccountMapper; 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AccountController {

    private final JpaUtils jpaUtils = new JpaUtils(List.of("email","username","lastLoginAt","createdAt","updatedAt"));
    private final IAccountService accountService;
    private final AccountMapper accountMapper;

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
                            schema = @Schema(implementation = AccountResponse.class)
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
        EnvelopeResponse<AccountResponse> success = EnvelopeResponse.success(accountMapper.toResponse(account));
        return ResponseEntity.status(HttpStatus.CREATED).body(success);
    }

    @PutMapping(value = "/{publicId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Actualizar cuenta de usuario"
    )
    public ResponseEntity<EnvelopeResponse<AccountResponse>> update(
            @PathVariable UUID publicId,
            @Valid @ModelAttribute AccountUpdateRequest dto
    ) {
        Account account = accountService.accountUpdate(publicId, dto);
        EnvelopeResponse<AccountResponse> success = EnvelopeResponse.success(accountMapper.toResponse(account));
        return ResponseEntity.ok(success);
    }

    @GetMapping("/{publicId}")
    @Operation(
            summary = "Obtener cuenta por ID"
    )
    public ResponseEntity<EnvelopeResponse<AccountResponse>> getById(
            @PathVariable UUID publicId
    ) {
        Account account = accountService.findAccountByPublicId(publicId);
        return ResponseEntity.ok(EnvelopeResponse.success(accountMapper.toResponse(account)));
    }

    @DeleteMapping("/{publicId}")
    @Operation(
            summary = "Eliminar cuenta"
    )
    public ResponseEntity<Void> delete(
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
            @RequestParam(value = "page", defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = "size", defaultValue = PaginationConstants.DEFAULT_SIZE) int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = PaginationConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = PaginationConstants.DEFAULT_SORT_DIRECTION) String sortDirection
    ) {
        Pageable pageable = jpaUtils.createPageable(page, size, sortBy, sortDirection);
        Page<Account> accountsPage = accountService.fetchAllAccounts(pageable);
        Page<AccountResponse> pagedDtoResponse = accountsPage.map(accountMapper::toResponse);
        PagedResponse<AccountResponse> response = new PagedResponse<>(
                pagedDtoResponse.getContent(),
                pagedDtoResponse.getNumber(),
                pagedDtoResponse.getSize(),
                pagedDtoResponse.getTotalElements(),
                pagedDtoResponse.getTotalPages(),
                pagedDtoResponse.hasNext(),
                pagedDtoResponse.hasPrevious(),
                pagedDtoResponse.isFirst(),
                pagedDtoResponse.isLast()
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
