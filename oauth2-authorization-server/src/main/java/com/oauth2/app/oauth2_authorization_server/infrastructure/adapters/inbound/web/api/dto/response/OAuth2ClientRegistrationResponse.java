package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
// Updated import for ClientType
import com.oauth2.app.oauth2_authorization_server.domain.enums.ClientType;
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
    private ClientType clientType;
    private List<String> scopes;
    private String scope; // Comma-separated string version of scopes
    private String clientName;
    private String clientAuthenticationMethod;
    private String authorizationGrantType;
    private long accessTokenHours;
    private long refreshTokenDays;
    private String redirectUri;
    private String postLogoutRedirectUri;
}
