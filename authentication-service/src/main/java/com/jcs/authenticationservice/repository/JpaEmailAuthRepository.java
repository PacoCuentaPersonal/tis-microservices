package com.jcs.authenticationservice.repository;

import com.jcs.authenticationservice.entity.EmailAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEmailAuthRepository extends JpaRepository<EmailAuthEntity,Long> {
}
