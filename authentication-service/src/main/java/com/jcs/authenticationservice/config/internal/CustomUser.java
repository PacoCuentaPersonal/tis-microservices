package com.jcs.authenticationservice.config.internal;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;

@Getter
public class CustomUser extends User {
    private final Set<Integer> rolesId;

    public CustomUser(String username, String password, Set<Integer> rolesId, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.rolesId = rolesId;
    }

    public CustomUser(String username, String password, Set<Integer> rolesId, boolean enabled,
                      boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
                      Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.rolesId = rolesId;
    }


}