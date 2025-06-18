package com.oauth2.app.oauth2_authorization_server.config.security;

import com.oauth2.app.oauth2_authorization_server.models.entity.Account;
import com.oauth2.app.oauth2_authorization_server.models.entity.RolesPermissions;
import com.oauth2.app.oauth2_authorization_server.interfaces.repository.JpaAccountRepository;
import com.oauth2.app.oauth2_authorization_server.interfaces.repository.JpaRolesPermissionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountDetailsService implements UserDetailsService {

    private final JpaAccountRepository jpaAccountRepository;
    private final JpaRolesPermissionsRepository rolesPermissionsRepository;

    public AccountDetailsService(
            JpaAccountRepository jpaAccountRepository,
            JpaRolesPermissionsRepository rolesPermissionsRepository) {
        this.jpaAccountRepository = jpaAccountRepository;
        this.rolesPermissionsRepository = rolesPermissionsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = jpaAccountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + account.getRoles().getName()));
        List<RolesPermissions> rolePermissions = rolesPermissionsRepository
                .findByRoleIdAndActiveTrue(account.getRoles().getId());
        List<GrantedAuthority> permissionAuthorities = rolePermissions.stream()
                .map(rp -> new SimpleGrantedAuthority("PERMISSION_" + rp.getPermission().getCode()))
                .collect(Collectors.toList());
        authorities.addAll(permissionAuthorities);

        log.debug("User {} has authorities: {}", username, authorities);

        return new User(
                account.getEmail(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                authorities
        );
    }
}