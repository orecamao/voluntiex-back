package com.voluntiex.voluntiexBackend.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.voluntiex.voluntiexBackend.models.Oportunidad;
import com.voluntiex.voluntiexBackend.repositories.OportunidadRepository;
import com.voluntiex.voluntiexBackend.utils.OportunidadSpecification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OportunidadService {

    @Autowired
    private OportunidadRepository oportunidadRepository;

    public Oportunidad createOportunidad(Oportunidad oportunidad) {
        return oportunidadRepository.save(oportunidad);
    }

    public List<Oportunidad> getAllOportunidades() {
        return oportunidadRepository.findAll();
    }

    public Oportunidad getOportunidadById(Long id) {
        return oportunidadRepository.findById(id).orElseThrow();
    }

    public Oportunidad updateOportunidad(Long id, Oportunidad oportunidad) {
        Oportunidad existingOportunidad = oportunidadRepository.findById(id).orElseThrow();
        existingOportunidad.setTitulo(oportunidad.getTitulo());
        existingOportunidad.setDescripcion(oportunidad.getDescripcion());
        existingOportunidad.setUbicacion(oportunidad.getUbicacion());
        return oportunidadRepository.save(existingOportunidad);
    }

    public Boolean deleteOportunidad(Long id) {
        try {
            oportunidadRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("Error al eliminar oportunidad con id " + id, e);
            return false;
        }
    }

    public List<Oportunidad> filtrarOportunidades(String titulo, String categoria, String ubicacion,
            LocalDate fechaInicio, LocalDate fechaFin,
            Integer duracion, String tipo, String requisitos) {

        Specification<Oportunidad> specification = Specification.where(null);

        if (titulo != null && !titulo.isEmpty()) {
            specification = specification.and(OportunidadSpecification.filterByTitulo(titulo));
        }
        if (categoria != null && !categoria.isEmpty()) {
            specification = specification.and(OportunidadSpecification.filterByCategoria(categoria));
        }
        if (ubicacion != null && !ubicacion.isEmpty()) {
            specification = specification.and(OportunidadSpecification.filterByUbicacion(ubicacion));
        }
        if (fechaInicio != null) {
            specification = specification.and(OportunidadSpecification.filterByFechaInicio(fechaInicio));
        }
        if (fechaFin != null) {
            specification = specification.and(OportunidadSpecification.filterByFechaFin(fechaFin));
        }
        if (duracion != null) {
            specification = specification.and(OportunidadSpecification.filterByDuracion(duracion));
        }
        if (tipo != null && !tipo.isEmpty()) {
            specification = specification.and(OportunidadSpecification.filterByTipo(tipo));
        }
        if (requisitos != null && !requisitos.isEmpty()) {
            specification = specification.and(OportunidadSpecification.filterByRequisitos(requisitos));
        }

        return oportunidadRepository.findAll(specification);
    }
}
