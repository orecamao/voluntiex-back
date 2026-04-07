package com.voluntiex.voluntiexBackend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.voluntiex.voluntiexBackend.models.EstadoOportunidad;
import com.voluntiex.voluntiexBackend.models.Oportunidad;

@Repository
public interface OportunidadRepository extends JpaRepository<Oportunidad, Long>, JpaSpecificationExecutor<Oportunidad> {

    List<Oportunidad> findByEstadoOrderByFechaInicioAsc(EstadoOportunidad estado);

    List<Oportunidad> findByOrganizacionIdAndEstadoOrderByFechaInicioDesc(Long organizacionId, EstadoOportunidad estado);

    List<Oportunidad> findByBeneficiarioCreadorIdAndEstadoOrderByFechaInicioDesc(Long beneficiarioId, EstadoOportunidad estado);
}
