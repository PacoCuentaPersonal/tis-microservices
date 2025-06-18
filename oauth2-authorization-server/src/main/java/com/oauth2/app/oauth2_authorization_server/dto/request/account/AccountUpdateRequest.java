package com.oauth2.app.oauth2_authorization_server.dto.request.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Schema(description = "Request para actualizar una cuenta existente. Todos los campos son opcionales - solo enviar los que se desean modificar.")
public record AccountUpdateRequest(
        @Schema(
                description = "Nueva imagen de perfil. Si no se envía, se mantiene la imagen actual.",
                required = false,
                type = "string",
                format = "binary"
        )
        MultipartFile image,

        @Schema(
                description = "Nuevo correo electrónico. Si es null, se mantiene el actual.",
                example = "nuevoemail@ejemplo.com",
                required = false,
                nullable = true
        )
        @Email(message = "El formato del correo electrónico no es válido")
        @Size(max = 200, message = "El correo electrónico no puede exceder 200 caracteres")
        String email,

        @Schema(
                description = "Nueva contraseña. Si es null, se mantiene la actual.\n" +
                        "Requisitos:\n" +
                        "- Mínimo 8 caracteres\n" +
                        "- Al menos 1 letra mayúscula\n" +
                        "- Al menos 1 letra minúscula\n" +
                        "- Al menos 1 número\n" +
                        "- Al menos 1 carácter especial (@$!%*?&)",
                example = "NewPassword123!",
                required = false,
                nullable = true
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
        )
        @Size(max = 100, message = "La contraseña no puede exceder 100 caracteres")
        String password,

        @Schema(
                description = "Nuevo nombre de usuario. Si es null, se mantiene el actual.\n" +
                        "Requisitos:\n" +
                        "- Entre 3 y 50 caracteres\n" +
                        "- Solo letras, números y guiones bajos",
                example = "nuevoUsuario123",
                required = false,
                nullable = true
        )
        @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
        @Pattern(
                regexp = "^[a-zA-Z0-9_]+$",
                message = "El nombre de usuario solo puede contener letras, números y guiones bajos"
        )
        String username,

        @Schema(
                description = "Nuevo estado de verificación del email. Si es null, se mantiene el actual.",
                example = "true",
                required = false,
                nullable = true
        )
        Boolean emailVerified,

        @Schema(
                description = "ID del nuevo rol a asignar. Si es null, se mantiene el rol actual.",
                example = "550e8400-e29b-41d4-a716-446655440002",
                required = false,
                nullable = true
        )
        UUID publicRoleId
) {
    public boolean hasImage() { return image != null && !image.isEmpty(); }
    public boolean hasEmail() { return email != null && !email.isBlank(); }
    public boolean hasPassword() { return password != null && !password.isBlank(); }
    public boolean hasUsername() { return username != null && !username.isBlank(); }
    public boolean hasEmailVerified() { return emailVerified != null; }
    public boolean hasPublicRoleId() { return publicRoleId != null; }
}