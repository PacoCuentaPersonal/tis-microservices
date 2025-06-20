package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Schema(description = "Request para actualizar una cuenta existente. Todos los campos son opcionales - solo enviar los que se desean modificar.")
public record AccountUpdateRequest(
        @Schema(
                description = "Nueva imagen de perfil. Si no se envía, se mantiene la imagen actual.",
                type = "string",
                format = "binary"
        )
        MultipartFile image,

        @Schema(
                description = "Nuevo correo electrónico. Si es null, se mantiene el actual.",
                example = "nuevoemail@ejemplo.com",
                nullable = true
        )
        @Email(message = "El formato del correo electrónico no es válido")
        @Size(max = 200, message = "El correo electrónico no puede exceder 200 caracteres")
        String email,

        @Schema(
                description = """
                        Nueva contraseña. Si es null, se mantiene la actual.
                        Requisitos:
                        - Mínimo 8 caracteres
                        - Al menos 1 letra mayúscula
                        - Al menos 1 letra minúscula
                        - Al menos 1 número
                        - Al menos 1 carácter especial (@$!%*?&)""",
                example = "NewPassword123!",
                nullable = true
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z[0-9]@$!%*?&]{8,}$",
                message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
        )
        @Size(max = 100, message = "La contraseña no puede exceder 100 caracteres")
        String password,

        @Schema(
                description = """
                        Nuevo nombre de usuario. Si es null, se mantiene el actual.
                        Requisitos:
                        - Entre 3 y 50 caracteres
                        - Solo letras, números y guiones bajos""",
                example = "nuevoUsuario123",
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
                nullable = true
        )
        Boolean emailVerified,

        @Schema(
                description = "ID del nuevo rol a asignar. Si es null, se mantiene el rol actual.",
                example = "550e8400-e29b-41d4-a716-446655440002",
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
