package com.jcs.authenticationservice.repository;
import com.jcs.authenticationservice.entity.ModuleEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface JpaModuleRepository extends JpaRepository<ModuleEnt, Integer> {

    @Query("""
    SELECT DISTINCT m FROM ModuleEnt m
    JOIN FETCH m.subModules s
    JOIN s.permissions p
    JOIN p.rolePermissions rp
    WHERE rp.role.id IN :roleIds
    AND m.active = true
    AND s.active = true
    AND p.active = true
    AND rp.active = true
    ORDER BY m.name, s.name
    """)
    List<ModuleEnt> findAllModuleByRoleId(@Param("roleIds") Set<Integer> roleIds);
}
