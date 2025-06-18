package com.jcs.coreservice.service.reniec;

import com.jcs.coreservice.config.PeruDevsProperties;
import com.jcs.coreservice.exception.ApiProviderException;
import com.jcs.coreservice.model.rest.ResponseApiPeruDevs;
import com.jcs.coreservice.usecase.IApiProviderStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service(value = "peruDevsClientImpl")
public class PeruDevsStrategy implements IApiProviderStrategy {
    private final String QUERY_PARAM_DNI = "document";
    private final String QUERY_PARAM_TOKEN = "key";
    private final PeruDevsProperties peruDevsProperties;
    public PeruDevsStrategy(
            PeruDevsProperties peruDevsProperties) {
        this.peruDevsProperties = peruDevsProperties;
    }

    @Override
    public HttpHeaders createHeaders() {
        return HttpHeaders.EMPTY;
    }

    @Override
    public String buildRequestUrl(String identifier) {
        return UriComponentsBuilder.fromUriString(peruDevsProperties.getUrl())
                .path("api/v1/dni/complete")
                .queryParam(QUERY_PARAM_DNI, identifier)
                .queryParam(QUERY_PARAM_TOKEN, peruDevsProperties.getToken())
                .toUriString();

    }
    @Override
    public void validateResponse(ResponseEntity<?> response) throws ApiProviderException {
        // 1. Validar código HTTP primero
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ApiProviderException("Error en la API PERU.DEVS. Código: " + response.getStatusCode());
        }

        // 2. Validar que el cuerpo no sea nulo
        if (response.getBody() == null) {
            throw new ApiProviderException("La API PERU.DEVS devolvió un cuerpo vacío");
        }

        // 3. Validar la estructura de la respuesta
        try {
            if (response.getBody() instanceof ResponseApiPeruDevs) {
                ResponseApiPeruDevs apiResponse = (ResponseApiPeruDevs) response.getBody();

                if (!apiResponse.isEstado()) { // o getEstado() si no usas Lombok
                    throw new ApiProviderException(
                            "PERU.DEVS indica : " +
                                    (apiResponse.getMensaje() != null ? apiResponse.getMensaje() : "Sin mensaje de error")
                    );
                }
            } else {
                throw new ApiProviderException("La respuesta de PERU.DEVS tiene un formato inesperado");
            }
        } catch (ClassCastException e) {
            throw new ApiProviderException("Error al procesar la respuesta de PERU.DEVS", e);
        }
    }

    @Override
    public void validateInput(String identifier) throws ApiProviderException {
        if (identifier == null || identifier.length() != 8 || !identifier.matches("\\d+")) {
            throw new ApiProviderException("DNI debe contener exactamente 8 dígitos numéricos");
        }
    }
}
