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

import com.voluntiex.voluntiexBackend.models.Beneficiario;
import com.voluntiex.voluntiexBackend.services.BeneficiarioService;

@RestController
@RequestMapping("/beneficiarios")
public class BeneficiarioController {

    @Autowired
    private BeneficiarioService beneficiarioService;

    @PostMapping
    public Beneficiario createBeneficiario(@RequestBody Beneficiario beneficiario) {
        return beneficiarioService.createBeneficiario(beneficiario);
    }

    @GetMapping
    public List<Beneficiario> getAllBeneficiarios() {
        return beneficiarioService.getAllBeneficiarios();
    }

    @GetMapping("/{id}")
    public Beneficiario getBeneficiarioById(@PathVariable Long id) {
        return beneficiarioService.getBeneficiarioById(id).orElseThrow();
    }

    @PutMapping("/{id}")
    public Beneficiario updateBeneficiario(@PathVariable Long id, @RequestBody Beneficiario beneficiario) {
        return beneficiarioService.updateBeneficiario(id, beneficiario);
    }

    @DeleteMapping("/{id}")
    public String deleteBeneficiario(@PathVariable Long id) {
        boolean ok = beneficiarioService.deleteBeneficiario(id);
        if(ok) {
            return "Beneficiario con id " + id + " ha sido eliminado.";
        } else {
            return "Error al eliminar beneficiario con id " + id + ". Puede que no exista.";
        }
    }
}
