package com.voluntiex.voluntiexBackend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voluntiex.voluntiexBackend.models.EstadoSolicitud;
import com.voluntiex.voluntiexBackend.models.SolicitudVoluntariado;

@Repository
public interface SolicitudVoluntariadoRepository extends JpaRepository<SolicitudVoluntariado, Long> {

    boolean existsByOportunidadIdAndVoluntarioId(Long oportunidadId, Long voluntarioId);

    Optional<SolicitudVoluntariado> findByOportunidadIdAndVoluntarioId(Long oportunidadId, Long voluntarioId);

    List<SolicitudVoluntariado> findByOportunidadIdOrderByFechaSolicitudDesc(Long oportunidadId);

    List<SolicitudVoluntariado> findByVoluntarioIdOrderByFechaSolicitudDesc(Long voluntarioId);

    long countByOportunidadIdAndEstadoNot(Long oportunidadId, EstadoSolicitud estado);
}
