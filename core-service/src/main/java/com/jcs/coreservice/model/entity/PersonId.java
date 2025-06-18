package com.jcs.coreservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PersonId {
    @Column(name = "document_type_id",nullable = false)
    private Integer documentType;
    @Column(name = "document_number",nullable = false)
    private String documentNumber;}
