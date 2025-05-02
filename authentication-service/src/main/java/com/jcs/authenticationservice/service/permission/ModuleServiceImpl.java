package com.jcs.authenticationservice.service.permission;

import com.jcs.authenticationservice.dto.permission.response.ModuleResponse;
import com.jcs.authenticationservice.dto.permission.response.SubModuleResponse;
import com.jcs.authenticationservice.entity.ModuleEnt;
import com.jcs.authenticationservice.entity.SubModuleEntity;
import com.jcs.authenticationservice.repository.JpaModuleRepository;
import com.jcs.authenticationservice.usecase.permission.IModulePort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ModuleServiceImpl implements IModulePort {
    private final JpaModuleRepository jpaModuleRepository;

    public ModuleServiceImpl(JpaModuleRepository jpaModuleRepository) {
        this.jpaModuleRepository = jpaModuleRepository;
    }

    public List<ModuleResponse> getAllModuleWithRoleId(Set<Integer> roleId){
        List<ModuleEnt> moduleEntities = jpaModuleRepository.findAllModuleByRoleId(roleId);
        if (moduleEntities.isEmpty()) {
            throw new RuntimeException(String.format("No module assigned to role with id %d", roleId));
        }
        return moduleEntities.stream()
                .map(module -> new ModuleResponse(
                        module.getPublicId(),
                        module.getName(),
                        module.getIcon(),
                        convertSubModules(module.getSubModules())
                ))
                .collect(Collectors.toList());

    }
    private List<SubModuleResponse> convertSubModules(Set<SubModuleEntity> subModules) {
        return subModules.stream()
                .map(subModule -> new SubModuleResponse(
                        subModule.getPublicId(),
                        subModule.getName(),
                        subModule.getIcon()
                ))
                .collect(Collectors.toList());
    }

}
