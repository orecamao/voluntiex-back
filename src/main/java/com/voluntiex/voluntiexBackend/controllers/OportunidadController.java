package com.voluntiex.voluntiexBackend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voluntiex.voluntiexBackend.models.Oportunidad;
import com.voluntiex.voluntiexBackend.services.OportunidadService;

@RestController
@RequestMapping("/oportunidades")
public class OportunidadController {

    @Autowired
    private OportunidadService oportunidadService;

    @PostMapping
    public Oportunidad createOportunidad(@RequestBody Oportunidad oportunidad) {
        return oportunidadService.createOportunidad(oportunidad);
    }

    @GetMapping("/all")
    public List<Oportunidad> getAllOportunidades() {
        return oportunidadService.getAllOportunidades();
    }

    @GetMapping("/{id}")
    public Oportunidad getOportunidadById(@PathVariable Long id) {
        return oportunidadService.getOportunidadById(id);
    }

    @PutMapping("/{id}")
    public Oportunidad updateOportunidad(@PathVariable Long id, @RequestBody Oportunidad oportunidad) {
        return oportunidadService.updateOportunidad(id, oportunidad);
    }

    @DeleteMapping("/{id}")
    public String deleteOportunidad(@PathVariable Long id) {
        boolean ok = oportunidadService.deleteOportunidad(id);
        if (ok) {
            return "Oportunidad con id " + id + " ha sido eliminada.";
        } else {
            return "Error al eliminar oportunidad con id " + id + ". Puede que no exista.";
        }
    }

    @GetMapping()
    public List<Oportunidad> searchOportunidades(
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "ubicacion", required = false) String ubicacion,
            @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin,
            @RequestParam(value = "duracion", required = false) Integer duracion,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "requisitos", required = false) String requisitos
    ) {
        return oportunidadService.filtrarOportunidades(titulo, categoria, ubicacion, fechaInicio, fechaFin, duracion, tipo, requisitos);
    }
}
