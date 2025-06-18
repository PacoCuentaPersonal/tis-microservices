package com.jcs.gatewayservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthenticationResponse(
        String accessToken, String refreshToken, Long expiresAt
) {
}
