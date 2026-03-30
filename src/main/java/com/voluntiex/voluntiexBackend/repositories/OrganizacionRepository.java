package com.voluntiex.voluntiexBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voluntiex.voluntiexBackend.models.Organizacion;

@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, Long> {} 