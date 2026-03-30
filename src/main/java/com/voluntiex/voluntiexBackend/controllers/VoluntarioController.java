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

import com.voluntiex.voluntiexBackend.models.Voluntario;
import com.voluntiex.voluntiexBackend.services.VoluntarioService;

@RestController
@RequestMapping("/voluntarios")
public class VoluntarioController {

    @Autowired
    private VoluntarioService voluntarioService;

    @PostMapping
    public Voluntario createVoluntario(@RequestBody Voluntario voluntario) {
        return voluntarioService.createVoluntario(voluntario);
    }

    @GetMapping
    public List<Voluntario> getAllVoluntarios() {
        return voluntarioService.getAllVoluntarios();
    }

    @GetMapping("/{id}")
    public Voluntario getVoluntarioById(@PathVariable Long id) {
        return voluntarioService.getVoluntarioById(id);
    }

    @PutMapping("/{id}")
    public Voluntario updateVoluntario(@PathVariable Long id, @RequestBody Voluntario voluntario) {
        return voluntarioService.updateVoluntario(id, voluntario);
    }

    @DeleteMapping("/{id}")
    public String deleteVoluntario(@PathVariable Long id) {
        boolean ok = voluntarioService.deleteVoluntario(id);
        if (ok) {
            return "Voluntario con id " + id + " ha sido eliminado.";
        } else {
            return "Error al eliminar voluntario con id " + id + ". Puede que no exista.";
        }
    }
}
