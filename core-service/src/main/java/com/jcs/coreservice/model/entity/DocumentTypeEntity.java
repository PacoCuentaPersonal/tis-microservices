package com.jcs.coreservice.model.entity;

import com.jcs.coreservice.shared.AbstractEntityJpa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity()
@Table(name = "document_type",schema = "app")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTypeEntity extends AbstractEntityJpa<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "public_id")
    private UUID publicId;
    private String name;
    private boolean active;
    @Override
    public Integer getId() {
        return id;
    }
}


