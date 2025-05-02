package com.jcs.authenticationservice.service.auth;

import com.jcs.authenticationservice.entity.RoleEntity;
import com.jcs.authenticationservice.repository.JpaRoleRepository;
import com.jcs.authenticationservice.usecase.authorities.IAuthoritiesService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthoritiesServiceImpl implements IAuthoritiesService {
    private final JpaRoleRepository jpaRoleRepository;

    public AuthoritiesServiceImpl(JpaRoleRepository jpaRoleRepository) {
        this.jpaRoleRepository = jpaRoleRepository;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities(Long userId) {
        Set<String> authorities = new HashSet<>();
        List<RoleEntity> roles = jpaRoleRepository.findAllByIdOfUser(userId);

        for (RoleEntity role : roles) {
            authorities.add("ROLE_" + role.getName());
            role.getRolePermissions().stream()
                    .map(rolePermissionEntity -> rolePermissionEntity.getPermission().getName())
                    .forEach(authorities::add);
        }

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
