package com.jcs.authenticationservice.entity;

import com.jcs.authenticationservice.shared.jpa.BaseEntityWithPublicId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "province",schema = "app")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProvinceEntity extends BaseEntityWithPublicId<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;
}
