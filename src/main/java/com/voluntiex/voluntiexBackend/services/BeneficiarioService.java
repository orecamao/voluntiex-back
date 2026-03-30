package com.voluntiex.voluntiexBackend.services;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voluntiex.voluntiexBackend.models.Beneficiario;
import com.voluntiex.voluntiexBackend.repositories.BeneficiarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BeneficiarioService {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    public Beneficiario createBeneficiario(Beneficiario beneficiario) {
        return beneficiarioRepository.save(beneficiario);
    }

    public List<Beneficiario> getAllBeneficiarios() {
        return beneficiarioRepository.findAll();
    }

    public Optional<Beneficiario> getBeneficiarioById(Long id) {
        return beneficiarioRepository.findById(id);
    }

    public Beneficiario updateBeneficiario(Long id, Beneficiario beneficiario) {
        Beneficiario existingBeneficiario = beneficiarioRepository.findById(id).orElseThrow();
        existingBeneficiario.setNombre(beneficiario.getNombre());
        existingBeneficiario.setDescripcion(beneficiario.getDescripcion());
        existingBeneficiario.setContacto(beneficiario.getContacto());
        existingBeneficiario.setDireccion(beneficiario.getDireccion());
        existingBeneficiario.setEstado(beneficiario.getEstado());
        return beneficiarioRepository.save(existingBeneficiario);
    }

    public Boolean deleteBeneficiario(Long id) {
        try {
            if (beneficiarioRepository.existsById(id)) {
                beneficiarioRepository.deleteById(id);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Error al eliminar beneficiario con id " + id, e);
            return false;
        }
    }
}
