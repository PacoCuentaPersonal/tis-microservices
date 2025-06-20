package com.oauth2.app.oauth2_authorization_server.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
// Corrected DTO import
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.request.clientOauth2.OAuth2ClientRegistrationRequest;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.inbound.web.api.dto.response.OAuth2ClientRegistrationResponse; // Path corregido
import com.oauth2.app.oauth2_authorization_server.domain.enums.ClientType;
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

            String clientTypeStr = rs.getString("client_type");
            if (clientTypeStr != null) {
                try {
                    response.setClientType(ClientType.valueOf(clientTypeStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid client_type value in database: {}", clientTypeStr);
                }
            }

            String scopesStr = rs.getString("scopes");
            if (scopesStr != null && !scopesStr.isEmpty()) {
                response.setScopes(List.of(scopesStr.split(",")));
            }

            String tokenSettingsJson = rs.getString("token_settings");
            if (tokenSettingsJson != null) {
                try {
                    Map<String, Object> tokenSettings = objectMapper.readValue(tokenSettingsJson, Map.class);
                    Object accessTokenTtl = tokenSettings.get("settings.token.access-token-time-to-live");
                    if (accessTokenTtl instanceof List && ((List<?>) accessTokenTtl).size() > 1) {
                        Number seconds = (Number) ((List<?>) accessTokenTtl).get(1);
                        response.setAccessTokenHours(seconds.longValue() / 3600);
                    }

                    Object refreshTokenTtl = tokenSettings.get("settings.token.refresh-token-time-to-live");
                    if (refreshTokenTtl instanceof List && ((List<?>) refreshTokenTtl).size() > 1) {
                        Number seconds = (Number) ((List<?>) refreshTokenTtl).get(1);
                        response.setRefreshTokenDays(seconds.longValue() / 86400);
                    }
                } catch (Exception e) {
                    log.error("Error parsing token settings for client {}: {}", response.getClientId(), e.getMessage());
                }
            }
            return response;
        };
        return jdbcTemplate.query(querySql, rowMapper);
    }

    public void delete(String id){
        log.info("process delete oauth2 with id : {}",id);
        String querySql = "DELETE FROM oauth2.oauth2_registered_client WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(querySql, id);
        if (rowsAffected == 0){
            throw new IllegalArgumentException("Failed delete oauth2 client registered : "+id+" because id not found");
        }
    }

    public OAuth2ClientRegistrationResponse update(OAuth2ClientRegistrationRequest request){
        log.info("process update oauth2 client registration for client ID: {}", request.getClientId());
        RegisteredClient existsRegisteredClient = registeredClientRepository.findByClientId(request.getClientId());
        if (Objects.isNull(existsRegisteredClient)){
            throw new IllegalArgumentException("Client ID not found : "+request.getClientId());
        }

        if (request.getAccessTokenHours() <= 0 || request.getRefreshTokenDays() <= 0){
            throw new IllegalArgumentException("Access Token hours and Refresh Token days must be positive");
        }

        RegisteredClient.Builder updateBuilder = RegisteredClient.from(existsRegisteredClient)
                .redirectUris(uris -> {
                    uris.clear();
                    uris.add(request.getRedirectUri());
                })
                .scopes(scs -> {
                    scs.clear();
                    scs.addAll(request.getScopes());
                });

        if (request.getClientType() == ClientType.PUBLIC) {
            updateBuilder
                    .clientSecret(null) 
                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    .clientSettings(ClientSettings.builder().requireProofKey(true).requireAuthorizationConsent(false).build());
        } else { 
             updateBuilder
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientSettings(ClientSettings.builder().requireProofKey(false).requireAuthorizationConsent(false).build());
        }

        updateBuilder.tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(request.getAccessTokenHours()))
                .refreshTokenTimeToLive(Duration.ofDays(request.getRefreshTokenDays())).build());

        RegisteredClient updatedRegisteredClient = updateBuilder.build();
        registeredClientRepository.save(updatedRegisteredClient);

        String updateTypeSql = "UPDATE oauth2.oauth2_registered_client SET client_type = ? WHERE id = ?";
        jdbcTemplate.update(updateTypeSql, request.getClientType().name(), existsRegisteredClient.getId());

        return OAuth2ClientRegistrationResponse.builder()
                .id(existsRegisteredClient.getId())
                .clientId(updatedRegisteredClient.getClientId())
                .clientType(request.getClientType()) 
                .redirectUri(request.getRedirectUri())
                .accessTokenHours(request.getAccessTokenHours())
                .refreshTokenDays(request.getRefreshTokenDays())
                .scopes(request.getScopes())
                .scope(String.join(",", request.getScopes()))
                .clientName(updatedRegisteredClient.getClientName()) // Added clientName
                .build();
    }

    public OAuth2ClientRegistrationResponse save(OAuth2ClientRegistrationRequest request){
        if (request.getId() != null && !request.getId().trim().isEmpty()){
            log.info("Request received with ID, proceeding to update client: {}", request.getId());
            return update(request);
        }
        
        log.info("Process create oauth2 client registration for client ID: {}", request.getClientId());
        if (registeredClientRepository.findByClientId(request.getClientId()) != null) {
            throw new IllegalArgumentException("Client ID already exists: " + request.getClientId());
        }
        if (request.getClientType() == null) {
            throw new IllegalArgumentException("Client type is required");
        }
        if (request.getAccessTokenHours() <= 0 || request.getRefreshTokenDays() <= 0){
            throw new IllegalArgumentException("Access Token hours and Refresh Token days must be positive");
        }

        String generatedId = UUID.randomUUID().toString();
        RegisteredClient.Builder clientBuilder = RegisteredClient.withId(generatedId)
                .clientId(request.getClientId())
                .clientName(request.getClientName() != null ? request.getClientName() : request.getClientId())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(request.getRedirectUri())
                .scopes(scs -> scs.addAll(request.getScopes()))
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(request.getAccessTokenHours()))
                        .refreshTokenTimeToLive(Duration.ofDays(request.getRefreshTokenDays())).build());

        if (request.getClientType() == ClientType.PUBLIC) {
            clientBuilder
                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    .clientSettings(ClientSettings.builder().requireProofKey(true).requireAuthorizationConsent(false).build());
        } else { 
            if (request.getClientSecret() == null || request.getClientSecret().trim().isEmpty()) {
                throw new IllegalArgumentException("Client secret is required for confidential clients");
            }
            clientBuilder
                    .clientSecret(passwordEncoder.encode(request.getClientSecret()))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
            if (request.isEnableClientCredentials()) {
                clientBuilder.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
            }
            clientBuilder.clientSettings(ClientSettings.builder().requireProofKey(false).requireAuthorizationConsent(false).build());
        }

        RegisteredClient registeredClient = clientBuilder.build();
        registeredClientRepository.save(registeredClient);

        String updateTypeSql = "UPDATE oauth2.oauth2_registered_client SET client_type = ? WHERE id = ?";
        jdbcTemplate.update(updateTypeSql, request.getClientType().name(), generatedId);

        return OAuth2ClientRegistrationResponse.builder()
                .id(generatedId)
                .clientId(registeredClient.getClientId())
                .clientType(request.getClientType())
                .redirectUri(request.getRedirectUri())
                .accessTokenHours(request.getAccessTokenHours())
                .refreshTokenDays(request.getRefreshTokenDays())
                .scopes(request.getScopes())
                .scope(String.join(",", request.getScopes()))
                .clientName(registeredClient.getClientName())
                .build();
    }
}
