package com.oauth2.app.oauth2_authorization_server.infrastructure.config.security;

import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.AccountEntity;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity.RolesPermissionsEntity;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.repository.SpringDataJpaAccountRepository;
import com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.repository.SpringDataJpaRolesPermissionsRepository;
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

@Slf4j()
@Service()
public class AccountDetailsService implements UserDetailsService {

    private final SpringDataJpaAccountRepository jpaAccountRepository;
    private final SpringDataJpaRolesPermissionsRepository rolesPermissionsRepository;

    public AccountDetailsService(
            SpringDataJpaAccountRepository jpaAccountRepository,
            SpringDataJpaRolesPermissionsRepository rolesPermissionsRepository) {
        this.jpaAccountRepository = jpaAccountRepository;
        this.rolesPermissionsRepository = rolesPermissionsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountEntity account = jpaAccountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + account.getRole().getName()));

        List<RolesPermissionsEntity> rolePermissions = rolesPermissionsRepository
                .findByRole_IdAndActiveTrue(account.getRole().getId()); 

        List<GrantedAuthority> permissionAuthorities = rolePermissions.stream()
                .map(rp -> new SimpleGrantedAuthority("PERMISSION_" + rp.getPermission().getCode()))
                .collect(Collectors.toList());
        authorities.addAll(permissionAuthorities);

        log.debug("User {} has authorities: {}", username, authorities);

        return new User(
                account.getEmail(),
                account.getPassword(),
                account.isActive(), 
                true,
                true,
                true,
                authorities
        );
    }
}
