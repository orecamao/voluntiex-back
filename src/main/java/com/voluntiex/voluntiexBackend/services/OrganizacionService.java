package com.voluntiex.voluntiexBackend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voluntiex.voluntiexBackend.models.Organizacion;
import com.voluntiex.voluntiexBackend.repositories.OrganizacionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrganizacionService {

    @Autowired
    private OrganizacionRepository organizacionRepository;

    public Organizacion createOrganizacion(Organizacion organizacion) {
        return organizacionRepository.save(organizacion);
    }

    public List<Organizacion> getAllOrganizaciones() {
        return organizacionRepository.findAll();
    }

    public Organizacion getOrganizacionById(Long id) {
        return organizacionRepository.findById(id).orElseThrow();
    }

    public Organizacion updateOrganizacion(Long id, Organizacion organizacion) {
        Organizacion existingOrganizacion = organizacionRepository.findById(id).orElseThrow();
        existingOrganizacion.setNombre(organizacion.getNombre());
        existingOrganizacion.setEmail(organizacion.getEmail());
        existingOrganizacion.setMision(organizacion.getMision());
        existingOrganizacion.setProyectos(organizacion.getProyectos());
        return organizacionRepository.save(existingOrganizacion);
    }

    public Boolean deleteOrganizacion(Long id) {
        try {
            organizacionRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("Error al eliminar organización con id " + id, e);
            return false;
        }
    }
}
