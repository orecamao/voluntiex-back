package com.voluntiex.voluntiexBackend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voluntiex.voluntiexBackend.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByEmailIgnoreCase(String email);

  boolean existsByEmailIgnoreCase(String email);

}
