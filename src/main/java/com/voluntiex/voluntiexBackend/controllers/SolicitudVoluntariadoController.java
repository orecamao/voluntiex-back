package com.voluntiex.voluntiexBackend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voluntiex.voluntiexBackend.models.SolicitudVoluntariado;
import com.voluntiex.voluntiexBackend.services.SolicitudVoluntariadoService;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudVoluntariadoController {

    @Autowired
    private SolicitudVoluntariadoService solicitudVoluntariadoService;

    @PostMapping("/oportunidades/{oportunidadId}")
    public ResponseEntity<?> postularse(@PathVariable Long oportunidadId, Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(solicitudVoluntariadoService.postularse(oportunidadId, authentication));
        } catch (AccessDeniedException e) {
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/oportunidades/{oportunidadId}")
    public ResponseEntity<?> getSolicitudesPorOportunidad(@PathVariable Long oportunidadId, Authentication authentication) {
        try {
            List<SolicitudVoluntariado> solicitudes = solicitudVoluntariadoService
                    .getSolicitudesPorOportunidad(oportunidadId, authentication);
            return ResponseEntity.ok(solicitudes);
        } catch (AccessDeniedException e) {
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/mias")
    public ResponseEntity<?> getMisSolicitudes(Authentication authentication) {
        try {
            return ResponseEntity.ok(solicitudVoluntariadoService.getSolicitudesDelVoluntario(authentication));
        } catch (AccessDeniedException e) {
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/{solicitudId}/cancelar")
    public ResponseEntity<?> cancelarPostulacion(@PathVariable Long solicitudId, Authentication authentication) {
        try {
            return ResponseEntity.ok(solicitudVoluntariadoService.cancelarPostulacion(solicitudId, authentication));
        } catch (AccessDeniedException e) {
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{solicitudId}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long solicitudId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            String estado = request.get("estado");
            return ResponseEntity.ok(solicitudVoluntariadoService.actualizarEstado(solicitudId, estado, authentication));
        } catch (AccessDeniedException e) {
            return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}
