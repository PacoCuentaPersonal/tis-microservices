package com.jcs.authenticationservice.dto.permission.response;

import lombok.*;

import java.util.UUID;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubModuleResponse {
    private UUID publicId;
    private String name;
    private String icon;
}
