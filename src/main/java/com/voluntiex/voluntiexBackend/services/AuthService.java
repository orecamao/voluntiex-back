package com.voluntiex.voluntiexBackend.services;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.voluntiex.voluntiexBackend.models.Usuario;
import com.voluntiex.voluntiexBackend.repositories.UsuarioRepository;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario register(Usuario usuario) {
        String normalizedEmail = normalizeEmail(usuario.getEmail());
        if (usuarioRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        usuario.setEmail(normalizedEmail);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public boolean login(String email, String password) {
        return usuarioRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .map(usuario -> passwordEncoder.matches(password, usuario.getPassword()))
                .orElse(false);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
