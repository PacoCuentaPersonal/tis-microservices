package com.jcs.authenticationservice.entity;

import com.jcs.authenticationservice.shared.jpa.BaseEntityWithPublicId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company", schema = "app")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyEntity extends BaseEntityWithPublicId<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String ruc;
    @Column(name = "company_name", nullable = false)
    private String companyName;
    @Column(name = "trade_name", nullable = false)
    private String tradeName;
    @Column(name = "company_type", nullable = false)
    private String companyType;
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    private DistrictEntity district;


}
