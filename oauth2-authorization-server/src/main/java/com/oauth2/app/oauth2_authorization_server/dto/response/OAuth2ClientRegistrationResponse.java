package com.oauth2.app.oauth2_authorization_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuth2ClientRegistrationResponse {

    private String id;
    private String clientId;
    // Removemos clientSecret del response - no debe mostrarse
    private ClientType clientType;
    private List<String> scopes;
    private String scope;
    private String clientName;
    private String clientAuthenticationMethod;
    private String authorizationGrantType;
    private long accessTokenHours;
    private long refreshTokenDays;
    private String redirectUri;
    private String postLogoutRedirectUri; // Nuevo campo
}