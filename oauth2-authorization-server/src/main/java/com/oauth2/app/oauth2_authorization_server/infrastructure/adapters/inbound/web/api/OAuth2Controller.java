package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api;

import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.clientOauth2.OAuth2ClientRegistrationRequest;
// Corrected DTO response import
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response.OAuth2ClientRegistrationResponse; 
import com.oauth2.app.oauth2_authorization_server.application.service.OAuth2ClientRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2ClientRegistrationService oAuth2ClientRegistrationService;

    @PostMapping(value = "/client-registration")
    public ResponseEntity<OAuth2ClientRegistrationResponse> saveOauth2ClientRegistration(@RequestBody OAuth2ClientRegistrationRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(oAuth2ClientRegistrationService.save(request));
    }
}
