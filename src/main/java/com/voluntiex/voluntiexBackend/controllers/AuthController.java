package com.voluntiex.voluntiexBackend.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
            if (authService.login(loginRequest.getEmail(), loginRequest.getPassword())) {
                String token = JwtTokenUtil.generateToken(loginRequest.getEmail());

                response.put("message", "Login exitoso");
                response.put("token", "Bearer " + token); 
    
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Credenciales incorrectas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
