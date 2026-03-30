package com.voluntiex.voluntiexBackend.utils;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.voluntiex.voluntiexBackend.models.Oportunidad;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class OportunidadSpecification {

    public static Specification<Oportunidad> filterByTitulo(String titulo) {
        return (Root<Oportunidad> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (titulo != null && !titulo.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%");
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Oportunidad> filterByCategoria(String categoria) {
        return (Root<Oportunidad> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (categoria != null && !categoria.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("categoria")), "%" + categoria.toLowerCase() + "%");
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Oportunidad> filterByUbicacion(String ubicacion) {
        return (Root<Oportunidad> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (ubicacion != null && !ubicacion.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("ubicacion")), "%" + ubicacion.toLowerCase() + "%");
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Oportunidad> filterByFechaInicio(LocalDate fechaInicio) {
        return (Root<Oportunidad> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (fechaInicio != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("fechaInicio"), fechaInicio);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Oportunidad> filterByFechaFin(LocalDate fechaFin) {
        return (Root<Oportunidad> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (fechaFin != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("fechaFin"), fechaFin);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Oportunidad> filterByDuracion(Integer duracion) {
        return (Root<Oportunidad> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (duracion != null) {
                return criteriaBuilder.equal(root.get("duracion"), duracion);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Oportunidad> filterByTipo(String tipo) {
        return (Root<Oportunidad> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (tipo != null && !tipo.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("tipo")), "%" + tipo.toLowerCase() + "%");
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Oportunidad> filterByRequisitos(String requisitos) {
        return (Root<Oportunidad> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (requisitos != null && !requisitos.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("requisitos")), "%" + requisitos.toLowerCase() + "%");
            }
            return criteriaBuilder.conjunction();
        };
    }
}
