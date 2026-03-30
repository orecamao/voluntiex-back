package com.voluntiex.voluntiexBackend.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voluntiex.voluntiexBackend.models.Organizacion;
import com.voluntiex.voluntiexBackend.services.OrganizacionService;

@RestController
@RequestMapping("/organizaciones")
public class OrganizacionController {

    @Autowired
    private OrganizacionService organizacionService;

    @PostMapping
    public Organizacion createOrganizacion(@RequestBody Organizacion organizacion) {
        return organizacionService.createOrganizacion(organizacion);
    }

    @GetMapping
    public List<Organizacion> getAllOrganizaciones() {
        return organizacionService.getAllOrganizaciones();
    }

    @GetMapping("/{id}")
    public Organizacion getOrganizacionById(@PathVariable Long id) {
        return organizacionService.getOrganizacionById(id);
    }

    @PutMapping("/{id}")
    public Organizacion updateOrganizacion(@PathVariable Long id, @RequestBody Organizacion organizacion) {
        return organizacionService.updateOrganizacion(id, organizacion);
    }

    @DeleteMapping("/{id}")
    public String deleteOrganizacion(@PathVariable Long id) {
        boolean ok = organizacionService.deleteOrganizacion(id);
        if (ok) {
            return "Organización con id " + id + " ha sido eliminada.";
        } else {
            return "Error al eliminar organización con id " + id + ". Puede que no exista.";
        }
    }
}
