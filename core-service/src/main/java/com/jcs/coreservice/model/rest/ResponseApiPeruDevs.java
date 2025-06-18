package com.jcs.coreservice.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseApiPeruDevs {
    private boolean estado;
    private String mensaje;
    private PersonaResultado resultado;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PersonaResultado {
        @JsonProperty("id") private String id;
        @JsonProperty("nombres") private String nombres;
        @JsonProperty("apellido_paterno") private String apellidoPaterno;
        @JsonProperty("apellido_materno") private String apellidoMaterno;
        @JsonProperty("fecha_nacimiento") private String fechaNacimiento;
        @JsonProperty("nombre_completo") private String nombreCompleto;
        @JsonProperty("genero") private String genero;
        @JsonProperty("codigo_verificacion") private String codigoVerificacion;
    }
}