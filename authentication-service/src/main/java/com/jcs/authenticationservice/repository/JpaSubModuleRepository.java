package com.jcs.authenticationservice.repository;

import com.jcs.authenticationservice.entity.SubModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaSubModuleRepository extends JpaRepository<SubModuleEntity,Integer> {

}
