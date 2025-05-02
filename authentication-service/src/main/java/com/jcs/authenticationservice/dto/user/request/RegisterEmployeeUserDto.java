package com.jcs.authenticationservice.dto.user.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterEmployeeUserDto {
    private String email;
    private String password;
    private boolean passwordPolicy;
    private UUID rolePublicId;

}
