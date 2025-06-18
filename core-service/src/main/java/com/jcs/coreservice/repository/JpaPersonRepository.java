package com.jcs.coreservice.repository;

import com.jcs.coreservice.model.entity.PersonEntity;
import com.jcs.coreservice.model.entity.PersonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaPersonRepository extends JpaRepository<PersonEntity, PersonId> {

    @Query("""
           SELECT p FROM PersonEntity p
           WHERE p.id.documentNumber = :dni
           AND p.active = true
           """)
    Optional<PersonEntity> findByDni(@Param("dni") String dni);
}
