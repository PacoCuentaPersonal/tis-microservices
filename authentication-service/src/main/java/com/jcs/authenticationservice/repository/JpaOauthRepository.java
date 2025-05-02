package com.jcs.authenticationservice.repository;

import com.jcs.authenticationservice.entity.OAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOauthRepository extends JpaRepository<OAuthEntity, Long> {

}
