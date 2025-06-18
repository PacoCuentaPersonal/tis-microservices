package com.oauth2.app.oauth2_authorization_server.models.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "account", schema = "oauth2")
public class Account extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id",nullable = false)
    @UuidGenerator
    private UUID publicId;

    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(nullable = false, name = "email_verified")
    private boolean emailVerified ;

    @Column(name = "profile_picture_url", nullable = true, length = 500)
    private String profilePictureUrl;

    @Column(name = "employee_public_id",nullable = true)
    private UUID employeePublicId;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Roles roles;



}