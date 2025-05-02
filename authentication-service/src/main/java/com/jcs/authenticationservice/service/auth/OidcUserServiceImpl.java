package com.jcs.authenticationservice.service.auth;

import com.jcs.authenticationservice.entity.OAuthEntity;
import com.jcs.authenticationservice.entity.UserEntity;
import com.jcs.authenticationservice.repository.JpaOauthRepository;
import com.jcs.authenticationservice.repository.JpaUserRepository;
import com.jcs.authenticationservice.service.permission.UserRoleServiceImpl;
import com.jcs.authenticationservice.usecase.authorities.IAuthoritiesService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class OidcUserServiceImpl implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final JpaOauthRepository jpaOauthRepository;
    private final JpaUserRepository jpaUserRepository;
    private final IAuthoritiesService authenticationPort;
    private final UserRoleServiceImpl userRoleService;


    public OidcUserServiceImpl(JpaOauthRepository jpaOauthRepository, JpaUserRepository jpaUserRepository, IAuthoritiesService authenticationPort, UserRoleServiceImpl userRoleService) {
        this.jpaOauthRepository = jpaOauthRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.authenticationPort = authenticationPort;
        this.userRoleService = userRoleService;
    }

    @Override
    @Transactional(readOnly = true)
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcIdToken userOauth2 = userRequest.getIdToken();
        String email = userOauth2.getEmail();
        UserEntity user = jpaUserRepository.findByEmail(email)
                .orElseGet(() -> save(userOauth2));


        Set<GrantedAuthority> authorities = authenticationPort.getAuthorities(user.getId());

        //can change info to info of db
        OidcUserInfo userInfo = OidcUserInfo
                .builder()
                .subject(userOauth2.getSubject())
                .email(userOauth2.getEmail())
                .name(userOauth2.getGivenName())
                .familyName(userOauth2.getFamilyName())
                .picture(userOauth2.getPicture())
                .emailVerified(userOauth2.getEmailVerified())
                .build();


        return new DefaultOidcUser(
                authorities,
                userRequest.getIdToken(),
                userInfo,
                "sub"
        );
    }

    @Transactional(readOnly = false)
    public UserEntity save(OidcIdToken userRequestInfo){
        OAuthEntity OauthToSave =  OAuthEntity.builder()
                        .sub(userRequestInfo.getSubject())
                        .email(userRequestInfo.getEmail())
                        .build();
        OAuthEntity OauthSaved = jpaOauthRepository.save(OauthToSave);
        UserEntity user =  UserEntity
                .builder()
                .profilePicture(userRequestInfo.getPicture())
                .active(Boolean.TRUE)
                .oauthAuth(OauthSaved)
                .build();
        userRoleService.registerDefault(user);

        return user;
    }

}


