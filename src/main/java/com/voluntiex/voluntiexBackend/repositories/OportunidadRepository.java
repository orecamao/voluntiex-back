package com.voluntiex.voluntiexBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.voluntiex.voluntiexBackend.models.Oportunidad;

@Repository
public interface OportunidadRepository extends JpaRepository<Oportunidad, Long>, JpaSpecificationExecutor<Oportunidad> {}