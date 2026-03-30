package com.voluntiex.voluntiexBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voluntiex.voluntiexBackend.models.Voluntario;

@Repository
public interface VoluntarioRepository extends JpaRepository<Voluntario, Long> {}