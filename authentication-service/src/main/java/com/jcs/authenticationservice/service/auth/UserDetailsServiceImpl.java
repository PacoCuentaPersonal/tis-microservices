package com.jcs.authenticationservice.service.auth;

import com.jcs.authenticationservice.config.internal.CustomUser;
import com.jcs.authenticationservice.entity.EmailAuthEntity;
import com.jcs.authenticationservice.entity.RoleEntity;
import com.jcs.authenticationservice.entity.UserEntity;
import com.jcs.authenticationservice.entity.UserRoleEntity;
import com.jcs.authenticationservice.repository.JpaUserRepository;
import com.jcs.authenticationservice.usecase.authorities.IAuthoritiesService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JpaUserRepository jpaUserRepository;
    private final IAuthoritiesService authenticationPort;

    public UserDetailsServiceImpl(JpaUserRepository jpaUserRepository, IAuthoritiesService authenticationPort) {
        this.jpaUserRepository = jpaUserRepository;
        this.authenticationPort = authenticationPort;
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = jpaUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Usuario %s no encontrado",email)));
        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(UserEntity user) {
        EmailAuthEntity emailAuthEntity = user.getEmailAuth();
        Set<Integer> rolesId = user.getUserRoles().stream().map(UserRoleEntity::getRole).map(RoleEntity::getId).collect(Collectors.toSet());
        String password = null;
        if (emailAuthEntity != null){
            password = emailAuthEntity.getPassword();
        } else {
            throw new UsernameNotFoundException("Credenciales no válidas");
        }
        return new CustomUser(
                user.getEmailAuth().getEmail(),
                password,
                rolesId,
                authenticationPort.getAuthorities(user.getId())
        );
    }

}
