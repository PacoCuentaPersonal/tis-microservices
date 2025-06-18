package com.jcs.coreservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]{2,}(\\s[A-ZÁÉÍÓÚÑ][a-záéíóúñ]{2,})*$",
            message = "Los nombres deben empezar con mayúscula, contener solo letras y cada palabra debe tener al menos 3 caracteres")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String names;

    @NotBlank(message = "El apellido paterno no puede estar vacío")
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]{2,}$",
            message = "El apellido paterno debe empezar con mayúscula y contener solo letras (mínimo 3 caracteres)")
    @Size(max = 50, message = "El apellido paterno no puede exceder los 50 caracteres")
    private String surnamePaternal;

    @NotBlank(message = "El apellido materno no puede estar vacío")
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]{2,}$",
            message = "El apellido materno debe empezar con mayúscula y contener solo letras (mínimo 3 caracteres)")
    @Size(max = 50, message = "El apellido materno no puede exceder los 50 caracteres")
    private String surnameMaternal;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private Date birthDate;

    @NotNull(message = "El género no puede estar vacío")
    @Pattern(regexp = "^[MF]$", message = "El género debe ser 'M' (masculino) o 'F' (femenino)")
    private String gender;

    @NotBlank(message = "El DNI no puede estar vacío")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 dígitos numéricos")
    private String dni;
}
