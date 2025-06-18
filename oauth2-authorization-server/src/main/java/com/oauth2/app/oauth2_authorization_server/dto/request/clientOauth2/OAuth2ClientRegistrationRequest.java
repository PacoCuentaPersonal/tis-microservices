package com.oauth2.app.oauth2_authorization_server.dto.request.clientOauth2;

import com.oauth2.app.oauth2_authorization_server.models.enums.ClientType;
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
    private String postLogoutRedirectUri; // Nuevo campo
    private boolean enableClientCredentials; // Solo para CONFIDENTIAL
}