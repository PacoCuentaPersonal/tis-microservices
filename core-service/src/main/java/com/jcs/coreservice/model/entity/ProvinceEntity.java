package com.jcs.coreservice.model.entity;

import com.jcs.coreservice.shared.AbstractEntityJpa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "province",schema = "app")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceEntity extends AbstractEntityJpa<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @UuidGenerator
    @Column(name = "public_id")
    private UUID publicId;
    @Column(name = "code_inei")
    private String codeInei;
    private String name;
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private DepartmentEntity departmentEntity;

    @Override
    public Integer getId() {
        return id;
    }
}
