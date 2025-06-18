package com.jcs.fileservice.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "image_categories",schema = "application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageCategories extends BaseAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @UuidGenerator
    @Column(name = "public_id", updatable = false, nullable = false, unique = true)
    private UUID publicId;
    private String name;
    private String description;
    private boolean active;
}
