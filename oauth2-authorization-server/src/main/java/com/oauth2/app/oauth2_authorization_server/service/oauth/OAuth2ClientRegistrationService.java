package com.oauth2.app.oauth2_authorization_server.service.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth2.app.oauth2_authorization_server.dto.request.clientOauth2.OAuth2ClientRegistrationRequest;
import com.oauth2.app.oauth2_authorization_server.dto.response.OAuth2ClientRegistrationResponse;
import com.oauth2.app.oauth2_authorization_server.models.enums.ClientType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2ClientRegistrationService {

    private final JdbcTemplate jdbcTemplate;
    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<OAuth2ClientRegistrationResponse> fetchOauth2Clients(){
        log.info("process fetch oauth2 clients..");

        String querySql = "SELECT id, client_id, client_secret, client_authentication_methods," +
                "authorization_grant_types, redirect_uris, scopes, client_name, token_settings, client_type FROM oauth2.oauth2_registered_client";

        RowMapper<OAuth2ClientRegistrationResponse> rowMapper = (rs, rowNum) -> {
            OAuth2ClientRegistrationResponse response = new OAuth2ClientRegistrationResponse();
            response.setId(rs.getString("id"));
            response.setClientId(rs.getString("client_id"));
            response.setClientAuthenticationMethod(rs.getString("client_authentication_methods"));
            response.setAuthorizationGrantType(rs.getString("authorization_grant_types"));
            response.setRedirectUri(rs.getString("redirect_uris"));
            response.setScope(rs.getString("scopes"));
            response.setClientName(rs.getString("client_name"));

            // Set client type
            String clientTypeStr = rs.getString("client_type");
            if (clientTypeStr != null) {
                response.setClientType(ClientType.valueOf(clientTypeStr));
            }

            // También establecer scopes como lista para compatibilidad
            String scopesStr = rs.getString("scopes");
            if (scopesStr != null && !scopesStr.isEmpty()) {
                response.setScopes(List.of(scopesStr.split(",")));
            }

            // Extract token settings
            String tokenSettingsJson = rs.getString("token_settings");
            if (tokenSettingsJson != null) {
                try {
                    Map<String, Object> tokenSettings = objectMapper.readValue(tokenSettingsJson, Map.class);

                    // Los valores están directamente en el mapa principal con prefijo "settings.token."
                    Object accessTokenTtl = tokenSettings.get("settings.token.access-token-time-to-live");
                    if (accessTokenTtl != null && accessTokenTtl instanceof List) {
                        List<?> ttlList = (List<?>) accessTokenTtl;
                        if (ttlList.size() > 1) {
                            // El segundo elemento es el valor en segundos
                            Number seconds = (Number) ttlList.get(1);
                            long hours = seconds.longValue() / 3600;
                            response.setAccessTokenHours(hours);
                            log.debug("Access token hours for {}: {}", response.getClientId(), hours);
                        }
                    }

                    Object refreshTokenTtl = tokenSettings.get("settings.token.refresh-token-time-to-live");
                    if (refreshTokenTtl != null && refreshTokenTtl instanceof List) {
                        List<?> ttlList = (List<?>) refreshTokenTtl;
                        if (ttlList.size() > 1) {
                            // El segundo elemento es el valor en segundos
                            Number seconds = (Number) ttlList.get(1);
                            long days = seconds.longValue() / 86400;
                            response.setRefreshTokenDays(days);
                            log.debug("Refresh token days for {}: {}", response.getClientId(), days);
                        }
                    }

                } catch (Exception e) {
                    log.error("Error parsing token settings for client {}: {}", response.getClientId(), e.getMessage());
                }
            }

            return response;
        };
        return jdbcTemplate.query(querySql, rowMapper);
    }

    // Helper method to parse ISO 8601 duration format (e.g., "PT1H" for 1 hour, "P9D" for 9 days)
    private long parseISO8601Duration(String duration) {
        try {
            Duration d = Duration.parse(duration);
            return d.getSeconds();
        } catch (Exception e) {
            log.error("Error parsing duration: {}", duration);
            return 0;
        }
    }

    public void delete(String id){
        log.info("process delete oauth2 with id : {}",id);
        String querySql = "DELETE FROM oauth2oauth2_registered_client WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(querySql, id);
        if (rowsAffected == 0){
            throw new IllegalArgumentException("Failed delete oauth2 client registered : "+id+" because id not found");
        }
    }

    public OAuth2ClientRegistrationResponse update(OAuth2ClientRegistrationRequest request){
        log.info("process update oauth2 client registration.");
        RegisteredClient existsRegisteredClient = registeredClientRepository.findByClientId(request.getClientId());
        if (Objects.isNull(existsRegisteredClient)){
            throw new IllegalArgumentException("Client ID not found : "+request.getClientId());
        }

        if (request.getAccessTokenHours() == 0 || request.getRefreshTokenDays() == 0){
            throw new IllegalArgumentException("Access Token or Refresh Token Days cannot be 0");
        }

        RegisteredClient.Builder updateBuilder = RegisteredClient.from(existsRegisteredClient)
                .redirectUris(redirects -> {
                    redirects.clear();
                    redirects.add(request.getRedirectUri());
                })
                .scopes(scopes -> {
                    scopes.clear();
                    scopes.addAll(request.getScopes());
                });

        // Configurar según el tipo de cliente
        if (request.getClientType() == ClientType.PUBLIC) {
            // Cliente público: sin secret, PKCE obligatorio
            updateBuilder
                    .clientSecret(null)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    .clientSettings(ClientSettings.builder()
                            .requireProofKey(true)  // PKCE obligatorio
                            .requireAuthorizationConsent(false)
                            .build());
        } else {
            // Cliente confidencial: mantener secret existente
            updateBuilder
                    .clientSecret(existsRegisteredClient.getClientSecret())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientSettings(ClientSettings.builder()
                            .requireProofKey(false)  // PKCE opcional
                            .requireAuthorizationConsent(false)
                            .build());
        }

        updateBuilder.tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(request.getAccessTokenHours()))
                .refreshTokenTimeToLive(Duration.ofDays(request.getRefreshTokenDays())).build());

        RegisteredClient updateRegistredClient = updateBuilder.build();
        registeredClientRepository.save(updateRegistredClient);

        // Update client_type in the database
        String updateTypeSql = "UPDATE oauth2.oauth2_registered_client SET client_type = ? WHERE id = ?";
        jdbcTemplate.update(updateTypeSql, request.getClientType().name(), existsRegisteredClient.getId());

        // Return the updated values
        return OAuth2ClientRegistrationResponse.builder()
                .id(existsRegisteredClient.getId())
                .clientId(request.getClientId())
                .clientType(request.getClientType())
                .redirectUri(request.getRedirectUri())
                .accessTokenHours(request.getAccessTokenHours())
                .refreshTokenDays(request.getRefreshTokenDays())
                .scopes(request.getScopes())
                .scope(String.join(",", request.getScopes()))
                .build();
    }

    public OAuth2ClientRegistrationResponse save(OAuth2ClientRegistrationRequest request){

        if (Objects.isNull(request.getId()) || request.getId().trim().isEmpty()){
            log.info("process create oauth2 client registration : {}",request);

            if (request.getClientType() == null) {
                throw new IllegalArgumentException("Client type is required");
            }

            String generatedId = UUID.randomUUID().toString();
            RegisteredClient.Builder clientBuilder = RegisteredClient.withId(generatedId)
                    .clientId(request.getClientId())
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri(request.getRedirectUri())
                    .scopes(scopRequest -> scopRequest.addAll(request.getScopes()))
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(Duration.ofHours(request.getAccessTokenHours()))
                            .refreshTokenTimeToLive(Duration.ofDays(request.getRefreshTokenDays())).build());

            String encodedSecret = null;

            // Configurar según el tipo de cliente
            if (request.getClientType() == ClientType.PUBLIC) {
                // Cliente público: sin secret, PKCE obligatorio
                clientBuilder
                        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                        .clientSettings(ClientSettings.builder()
                                .requireProofKey(true)  // PKCE obligatorio
                                .requireAuthorizationConsent(false)
                                .build());
            } else {
                // Cliente confidencial
                if (request.getClientSecret() == null || request.getClientSecret().trim().isEmpty()) {
                    throw new IllegalArgumentException("Client secret is required for confidential clients");
                }

                encodedSecret = passwordEncoder.encode(request.getClientSecret());
                clientBuilder
                        .clientSecret(encodedSecret)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

                // Agregar client_credentials si está habilitado
                if (request.isEnableClientCredentials()) {
                    clientBuilder.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
                }

                clientBuilder.clientSettings(ClientSettings.builder()
                        .requireProofKey(false)  // PKCE opcional para confidential
                        .requireAuthorizationConsent(false)
                        .build());
            }

            RegisteredClient registeredClient = clientBuilder.build();
            registeredClientRepository.save(registeredClient);

            // Update client_type in the database
            String updateTypeSql = "UPDATE oauth2.oauth2_registered_client SET client_type = ? WHERE id = ?";
            jdbcTemplate.update(updateTypeSql, request.getClientType().name(), generatedId);

            return OAuth2ClientRegistrationResponse.builder()
                    .id(generatedId)
                    .clientId(request.getClientId())
                    .clientType(request.getClientType())
                    .redirectUri(request.getRedirectUri())
                    .accessTokenHours(request.getAccessTokenHours())
                    .refreshTokenDays(request.getRefreshTokenDays())
                    .scopes(request.getScopes())
                    .scope(String.join(",", request.getScopes()))
                    .build();
        }
        return update(request);
    }
}