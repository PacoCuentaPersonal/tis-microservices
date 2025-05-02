package com.jcs.authenticationservice.service.permission;

import com.jcs.authenticationservice.entity.RoleEntity;
import com.jcs.authenticationservice.entity.UserEntity;
import com.jcs.authenticationservice.entity.UserRoleEntity;
import com.jcs.authenticationservice.entity.UserRoleId;
import com.jcs.authenticationservice.repository.JpaUserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRoleServiceImpl {

    private final RoleServiceImpl roleService;
    private final JpaUserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(RoleServiceImpl roleService, JpaUserRoleRepository userRoleRepository) {
        this.roleService = roleService;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional(readOnly = false)
    public void registerDefault(UserEntity user){
        RoleEntity customerRoleEntity =  roleService.getInstantCustomerRole();
        UserRoleEntity urToSave =  UserRoleEntity
                .builder()
                .id( new UserRoleId(user.getId(),customerRoleEntity.getId()))
                .user(user)
                .role(customerRoleEntity)
                .active(Boolean.TRUE)
                .build();
        userRoleRepository.save(urToSave);

    }
    @Transactional(readOnly = false)
    public void registerUserWithRole(UserEntity user, RoleEntity role){
        boolean b = validateUniqueAssignedRootRole();
        if (b){
            throw new RuntimeException("ROOT role is assigned only once");
        }
        UserRoleEntity urToSave =  UserRoleEntity
                .builder()
                .id( new UserRoleId(user.getId(),role.getId()))
                .user(user)
                .role(role)
                .active(Boolean.TRUE)
                .build();
        userRoleRepository.save(urToSave);

    }

    private boolean validateUniqueAssignedRootRole(){
        return userRoleRepository.isRootRoleAssigned();
    }



}
