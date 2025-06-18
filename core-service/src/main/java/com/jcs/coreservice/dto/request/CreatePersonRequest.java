package com.jcs.coreservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonRequest {
    @Valid
    @NotNull(message = "Es necesario proporcionar los datos de la persona")
    private PersonRequest personRequest;
    @NotNull(message = "Es necesario proporcionar un tipo de documento")
    private UUID pIdDocumentType;
    @NotNull(message = "Es necesario proporcionar un distrito")
    private UUID pIdDistrict;
}
