package com.jcs.authenticationservice.entity;

import com.jcs.authenticationservice.shared.jpa.BaseEntityWithPublicId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "action", schema = "app")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActionEntity extends BaseEntityWithPublicId<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private boolean active;
}
