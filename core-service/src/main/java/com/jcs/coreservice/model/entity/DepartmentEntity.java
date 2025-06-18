package com.jcs.coreservice.model.entity;

import com.jcs.coreservice.shared.AbstractEntityJpa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "department")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentEntity extends AbstractEntityJpa<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "public_id")
    private UUID publicId;
    @Column(name = "code_inei")
    private String codeInei;
    private String name;
    private boolean active;

    @Override
    public Integer getId() {
        return id;
    }
}
