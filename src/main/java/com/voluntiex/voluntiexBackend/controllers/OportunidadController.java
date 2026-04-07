package com.voluntiex.voluntiexBackend.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/mias")
    public ResponseEntity<?> getMyOportunidades(Authentication authentication) {
        try {
            return ResponseEntity.ok(oportunidadService.getMyOportunidades(authentication));
        } catch (AccessDeniedException e) {
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createOportunidad(@RequestBody Oportunidad oportunidad, Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(oportunidadService.createOportunidad(oportunidad, authentication));
        } catch (AccessDeniedException e) {
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
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
    public ResponseEntity<?> updateOportunidad(
            @PathVariable Long id,
            @RequestBody Oportunidad oportunidad,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(oportunidadService.updateOportunidad(id, oportunidad, authentication));
        } catch (AccessDeniedException e) {
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOportunidad(@PathVariable Long id, Authentication authentication) {
        try {
            oportunidadService.deleteOportunidad(id, authentication);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Oportunidad cancelada correctamente");
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
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

    private ResponseEntity<Map<String, String>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}
