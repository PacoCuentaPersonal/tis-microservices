package com.jcs.coreservice.service.reniec;

import com.jcs.coreservice.config.ApisNetPeProperties;
import com.jcs.coreservice.exception.ApiProviderException;
import com.jcs.coreservice.usecase.IApiProviderStrategy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ApisNetPeStrategy implements IApiProviderStrategy {

    private static final String DNI_QUERY_PARAM = "numero";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_FORMAT = "Bearer %s";

    private final ApisNetPeProperties apisNetPeProperties;

    public ApisNetPeStrategy(

            ApisNetPeProperties apisNetPeProperties) {
        this.apisNetPeProperties = apisNetPeProperties;
    }

    @Override
    public HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, String.format(BEARER_TOKEN_FORMAT, apisNetPeProperties.getToken()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Override
    public String buildRequestUrl(String dni) {
        return UriComponentsBuilder.fromUriString(apisNetPeProperties.getUrl())
                .path("v2/reniec/dni")
                .queryParam(DNI_QUERY_PARAM, dni)
                .toUriString();
    }

    @Override
    public void validateResponse(ResponseEntity<?> response) throws ApiProviderException {
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new ApiProviderException("Respuesta inválida de APIS.NET.PE: " + response.getStatusCode());
        }
    }

    @Override
    public void validateInput(String dni) throws ApiProviderException {
        if (dni == null || dni.length() != 8 || !dni.matches("\\d+")) {
            throw new ApiProviderException("DNI debe contener exactamente 8 dígitos numéricos");
        }
    }
}