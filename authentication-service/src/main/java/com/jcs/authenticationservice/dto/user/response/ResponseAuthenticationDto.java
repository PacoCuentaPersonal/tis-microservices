package com.jcs.authenticationservice.dto.user.response;

import com.jcs.authenticationservice.dto.permission.response.ModuleResponse;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAuthenticationDto {
    private List<ModuleResponse> modules;
    private List<String> pendingTask;
    private String email;
    private String avatarUrl;
}
