package com.jcs.authenticationservice.usecase.permission;

import com.jcs.authenticationservice.dto.permission.response.ModuleResponse;

import java.util.List;
import java.util.Set;

public interface IModulePort {
    List<ModuleResponse> getAllModuleWithRoleId(Set<Integer> RoleId);
}
