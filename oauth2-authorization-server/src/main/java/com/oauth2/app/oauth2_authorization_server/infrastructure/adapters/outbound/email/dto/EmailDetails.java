package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.email.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailDetails {
    private String to;
    private String subject;
    private String message;
    private String name;
}
