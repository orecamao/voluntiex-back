package com.voluntiex.voluntiexBackend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voluntiex.voluntiexBackend.models.Voluntario;
import com.voluntiex.voluntiexBackend.repositories.VoluntarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VoluntarioService {

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    public Voluntario createVoluntario(Voluntario voluntario) {
        return voluntarioRepository.save(voluntario);
    }

    public List<Voluntario> getAllVoluntarios() {
        return voluntarioRepository.findAll();
    }

    public Voluntario getVoluntarioById(Long id) {
        return voluntarioRepository.findById(id).orElseThrow();
    }

    public Voluntario updateVoluntario(Long id, Voluntario voluntario) {
        Voluntario existingVoluntario = voluntarioRepository.findById(id).orElseThrow();
        existingVoluntario.setNombre(voluntario.getNombre());
        existingVoluntario.setEmail(voluntario.getEmail());
        existingVoluntario.setHabilidades(voluntario.getHabilidades());
        existingVoluntario.setIntereses(voluntario.getIntereses());
        return voluntarioRepository.save(existingVoluntario);
    }

    public Boolean deleteVoluntario(Long id) {
        try {
            voluntarioRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("Error al eliminar voluntario con id " + id, e);
            return false;
        }
    }
}
