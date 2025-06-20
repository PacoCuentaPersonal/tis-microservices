package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Schema(description = "Request para crear una nueva cuenta de usuario")
public record AccountRequest(
        @Schema(
                description = "Imagen de perfil del usuario",
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "string",
                format = "binary"
        )
        @NotNull(message = "La imagen no puede ser nula")
        MultipartFile image,

        @Schema(
                description = "Correo electrónico del usuario",
                example = "usuario@ejemplo.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "El correo electrónico no puede ser nulo, ni vacío")
        @Email(message = "El formato del correo electrónico no es válido")
        @Size(max = 200, message = "El correo electrónico no puede exceder 200 caracteres")
        String email,

        @Schema(
                description = """
                        Contraseña del usuario.
                        Requisitos:
                        - Mínimo 8 caracteres
                        - Al menos 1 letra mayúscula
                        - Al menos 1 letra minúscula
                        - Al menos 1 número
                        - Al menos 1 carácter especial (@$!%*?&)""",
                example = "Password123!",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "La contraseña no puede ser nula, ni estar vacía")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z[0-9]@$!%*?&]{8,}$",
                message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
        )
        @Size(max = 100, message = "La contraseña no puede exceder 100 caracteres")
        String password,

        @Schema(
                description = """
                        Nombre de usuario único.
                        Requisitos:
                        - Entre 3 y 50 caracteres
                        - Solo letras, números y guiones bajos""",
                example = "usuario123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "El nombre de usuario no puede ser nulo, ni vacío")
        @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
        @Pattern(
                regexp = "^[a-zA-Z0-9_]+$",
                message = "El nombre de usuario solo puede contener letras, números y guiones bajos"
        )
        String username,

        @Schema(
                description = "Indica si el email del usuario ha sido verificado",
                example = "false",
                defaultValue = "false"
        )
        boolean emailVerified,

        @Schema(
                description = "ID público del rol a asignar al usuario",
                example = "550e8400-e29b-41d4-a716-446655440001",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Es necesario asignar un rol")
        UUID publicRoleId
) {
}
