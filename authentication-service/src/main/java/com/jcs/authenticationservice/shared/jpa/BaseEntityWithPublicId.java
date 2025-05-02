package com.jcs.authenticationservice.shared.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntityWithPublicId<T> extends BaseEntity<T> {
    @UuidGenerator()
    @Column(name = "public_id", nullable = false, updatable = false, columnDefinition = "UNIQUEIDENTIFIER")
    private UUID publicId = UUID.randomUUID();
}