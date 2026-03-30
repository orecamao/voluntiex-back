package com.voluntiex.voluntiexBackend.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "oportunidades")
@Getter
@Setter
public class Oportunidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String titulo;
    private String descripcion;
    private String ubicacion;
    private String categoria;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer duracion;
    private String tipo;
    private String requisitos;
    private String nombreUsuario;

    @ManyToOne
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;

    @ManyToMany
    @JoinTable(
            name = "oportunidades_beneficiarios",
            joinColumns = @JoinColumn(name = "oportunidad_id"),
            inverseJoinColumns = @JoinColumn(name = "beneficiario_id")
    )
    private List<Beneficiario> beneficiarios;
}
