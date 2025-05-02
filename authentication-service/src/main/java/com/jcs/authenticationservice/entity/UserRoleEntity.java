package com.jcs.authenticationservice.entity;

import com.jcs.authenticationservice.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_role", schema = "app")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleEntity extends BaseEntity<UserRoleId> {
    @EmbeddedId
    private UserRoleId id;
    private boolean active;
    @ManyToOne
    @MapsId(value = "roleId")
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @ManyToOne
    @MapsId(value = "userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;


}
