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
@Table(name = "district", schema = "app")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DistrictEntity extends AbstractEntityJpa<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @UuidGenerator
    @Column(name = "public_id")
    private UUID publicId;
    @Column(name = "ubigeo_inei")
    private String ubigeoInei;
    @Column(name = "ubigeo_reniec")
    private String ubigeoReniec;
    private String name;
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", referencedColumnName = "id")
    private ProvinceEntity provinceEntity;

    @Override
    public Integer getId() {
        return id;
    }
}
