package com.oauth2.app.oauth2_authorization_server.models;


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
