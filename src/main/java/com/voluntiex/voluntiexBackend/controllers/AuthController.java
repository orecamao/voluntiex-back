package com.voluntiex.voluntiexBackend.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voluntiex.voluntiexBackend.models.Usuario;
import com.voluntiex.voluntiexBackend.services.AuthService;
import com.voluntiex.voluntiexBackend.utils.JwtTokenUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Usuario usuario) {
        try {
            authService.register(usuario);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Registro exitoso");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Usuario loginRequest) {
        Map<String, String> response = new HashMap<>();

        try {
            Usuario usuario = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword()).orElse(null);
            if (usuario != null) {
                String token = JwtTokenUtil.generateToken(usuario.getEmail());

                response.put("message", "Login exitoso");
                response.put("token", "Bearer " + token);
                response.put("nombre", usuario.getNombre());
                response.put("email", usuario.getEmail());
                if (usuario.getRol() != null) {
                    response.put("rol", usuario.getRol());
                }
                if (usuario.getTipo() != null) {
                    response.put("tipo", usuario.getTipo());
                }
    
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Credenciales incorrectas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser(Authentication authentication) {
        Map<String, String> response = new HashMap<>();

        if (authentication == null || authentication.getName() == null) {
            response.put("message", "No autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Usuario usuario = authService.getUserByEmail(authentication.getName());
            response.put("nombre", usuario.getNombre());
            response.put("email", usuario.getEmail());
            if (usuario.getRol() != null) {
                response.put("rol", usuario.getRol());
            }
            if (usuario.getTipo() != null) {
                response.put("tipo", usuario.getTipo());
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Map<String, String> response = new HashMap<>();

        if (authentication == null || authentication.getName() == null) {
            response.put("message", "No autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            authService.changePassword(
                    authentication.getName(),
                    request.get("currentPassword"),
                    request.get("newPassword"));
            response.put("message", "Contrasena actualizada correctamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
