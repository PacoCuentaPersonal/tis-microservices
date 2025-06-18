package com.jcs.coreservice.model.entity;

import com.jcs.coreservice.shared.AbstractEntityJpa;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "person", schema = "app",
    indexes = {
        @Index(name = "UQ_person_public_id", columnList = "public_id", unique = true),
        @Index(name = "IX_person_names", columnList = "names"),
        @Index(name = "IX_person_paternal_surname", columnList = "paternal_surname"),
        @Index(name = "IX_person_maternal_surname", columnList = "maternal_surname"),
        @Index(name = "IX_person_district_id", columnList = "district_id")
    })
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PersonEntity extends AbstractEntityJpa<PersonId> {

    @EmbeddedId
    private PersonId id;
    @Column(name = "public_id", nullable = false)
    private UUID publicId;
    @Column(nullable = false)
    private String names;
    @Column(name = "paternal_surname",nullable = false)
    private String paternalSurname;
    @Column(name = "maternal_surname",nullable = false)
    private String maternalSurname;
    private Date birthdate;
    @Column(nullable = false)
    private Character gender;

    @Column(columnDefinition = "boolean default true")
    private boolean active;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId(value = "documentType")
    @JoinColumn(name = "document_type_id", referencedColumnName = "id")
    private DocumentTypeEntity documentType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    private DistrictEntity districtEntity;


}
