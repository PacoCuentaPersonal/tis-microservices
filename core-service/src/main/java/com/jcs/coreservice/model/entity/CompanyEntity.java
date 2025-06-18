package com.jcs.coreservice.model.entity;

import com.jcs.coreservice.shared.AbstractEntityJpa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "company", schema = "app")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanyEntity extends AbstractEntityJpa<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "public_id")
    private UUID publicId;
    private String ruc;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "trade_name")
    private String tradeName;
    @Column(name = "company_type")
    private String companyType;
    private boolean active;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    private DistrictEntity districtEntity;

    @Override
    public Integer getId() {
        return id;
    }

}
