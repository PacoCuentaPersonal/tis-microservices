package com.jcs.authenticationservice.dto.permission.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponse {
    private UUID publicId;
    private String name;
    private String icon;
    private List<SubModuleResponse> submodule;
}
