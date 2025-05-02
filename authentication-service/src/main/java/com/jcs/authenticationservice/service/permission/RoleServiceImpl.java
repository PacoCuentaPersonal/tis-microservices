package com.jcs.authenticationservice.service.permission;

import com.jcs.authenticationservice.entity.RoleEntity;
import com.jcs.authenticationservice.repository.JpaRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class RoleServiceImpl {
    private static final String CUSTOMER_ROLE = "CUSTOMER";

    private final JpaRoleRepository jpaRoleRepository;

    public RoleServiceImpl(JpaRoleRepository jpaRoleRepository) {
        this.jpaRoleRepository = jpaRoleRepository;
    }

    @Transactional(readOnly = true)
    public RoleEntity getInstantCustomerRole(){
        Optional<RoleEntity> optRole = jpaRoleRepository.findByName(RoleServiceImpl.CUSTOMER_ROLE);
        if (optRole.isEmpty()){
            throw new RuntimeException("Default role not instance in db");
        }
        return optRole.get();
    }

    /*
    * Logica de negocio - un unico admin por sistema
    * */
    @Transactional(readOnly = true)
    public RoleEntity getRoleByPublicId(UUID publicId){
        Optional<RoleEntity> optRole = jpaRoleRepository.findByPublicIdAndActiveTrue(publicId);
        if (optRole.isEmpty()){
            throw new RuntimeException("Role not found in db");
        }
        return optRole.get();
    }

}
