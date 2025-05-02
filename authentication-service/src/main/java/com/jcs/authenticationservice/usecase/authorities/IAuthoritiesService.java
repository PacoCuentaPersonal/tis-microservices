package com.jcs.authenticationservice.usecase.authorities;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public interface IAuthoritiesService {
    Set<GrantedAuthority>  getAuthorities (Long id);
}
