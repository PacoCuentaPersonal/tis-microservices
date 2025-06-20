package com.oauth2.app.oauth2_authorization_server.infrastructure.adapters.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "roles", schema = "oauth2")
public class RolesEntity extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;
}
