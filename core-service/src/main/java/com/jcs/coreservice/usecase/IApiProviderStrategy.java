package com.jcs.coreservice.usecase;

import com.jcs.coreservice.exception.ApiProviderException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface IApiProviderStrategy {
    HttpHeaders createHeaders();
    String buildRequestUrl(String identifier);
    void validateResponse(ResponseEntity<?> response) throws ApiProviderException;
    void validateInput(String identifier) throws ApiProviderException;
}