package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.clientOauth2;

import com.oauth2.app.oauth2_authorization_server.domain.enums.ClientType; // Updated import
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2ClientRegistrationRequest {

    private String id;
    private String clientId;
    private String clientSecret; // Solo requerido para CONFIDENTIAL
    private ClientType clientType;
    private List<String> scopes;
    private long accessTokenHours;
    private long refreshTokenDays;
    private String redirectUri;
    private String clientName; // Campo que faltaba y es importante
    private String postLogoutRedirectUri; 
    private boolean enableClientCredentials; // Solo para CONFIDENTIAL
}
