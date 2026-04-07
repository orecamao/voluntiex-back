package com.voluntiex.voluntiexBackend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voluntiex.voluntiexBackend.models.Beneficiario;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {

    Optional<Beneficiario> findByUsuarioEmailIgnoreCase(String email);

    Optional<Beneficiario> findByContactoIgnoreCase(String contacto);
}
