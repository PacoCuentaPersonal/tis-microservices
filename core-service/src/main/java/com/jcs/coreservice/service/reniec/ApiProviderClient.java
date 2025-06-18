package com.jcs.coreservice.service.reniec;

import com.jcs.coreservice.exception.ApiProviderException;
import com.jcs.coreservice.usecase.IApiProviderStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiProviderClient{
    private final RestTemplate restTemplate;
    private final IApiProviderStrategy providerStrategy;

    public ApiProviderClient(RestTemplate restTemplate,@Qualifier("peruDevsClientImpl") IApiProviderStrategy providerStrategy) {
        this.restTemplate = restTemplate;
        this.providerStrategy = providerStrategy;
    }

    public <T> T getProviderData(String identifier, Class<T> responseType) throws ApiProviderException {
        providerStrategy.validateInput(identifier);

        HttpHeaders headers = providerStrategy.createHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = providerStrategy.buildRequestUrl(identifier);

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    responseType
            );

            providerStrategy.validateResponse(response);
            return response.getBody();
        }
        catch (HttpServerErrorException e) {
            throw new ApiProviderException("Error en el servidor del proveedor: " + e.getStatusCode(), e);
        } catch (Exception e) {
            throw new ApiProviderException("Error inesperado al consultar el proveedor", e);
        }
    }
}
