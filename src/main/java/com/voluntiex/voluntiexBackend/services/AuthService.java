package com.voluntiex.voluntiexBackend.services;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voluntiex.voluntiexBackend.models.Beneficiario;
import com.voluntiex.voluntiexBackend.models.Organizacion;
import com.voluntiex.voluntiexBackend.models.Usuario;
import com.voluntiex.voluntiexBackend.models.Voluntario;
import com.voluntiex.voluntiexBackend.repositories.BeneficiarioRepository;
import com.voluntiex.voluntiexBackend.repositories.OrganizacionRepository;
import com.voluntiex.voluntiexBackend.repositories.UsuarioRepository;
import com.voluntiex.voluntiexBackend.repositories.VoluntarioRepository;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario register(Usuario usuario) {
        String normalizedEmail = normalizeEmail(usuario.getEmail());
        String normalizedTipo = normalizeTipo(usuario.getTipo());
        if (usuarioRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("El correo ya estÃ¡ registrado");
        }

        usuario.setEmail(normalizedEmail);
        usuario.setTipo(normalizedTipo);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Usuario savedUsuario = usuarioRepository.save(usuario);
        createProfileByTipo(savedUsuario);
        return savedUsuario;
    }

    public Optional<Usuario> authenticate(String email, String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseÃ±a es obligatoria");
        }

        return usuarioRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .filter(usuario -> passwordEncoder.matches(password, usuario.getPassword()));
    }

    public Usuario getUserByEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo de usuario es obligatorio");
        }

        String normalizedTipo = Normalizer.normalize(tipo.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);

        if (!normalizedTipo.equals("voluntario")
                && !normalizedTipo.equals("beneficiario")
                && !normalizedTipo.equals("organizacion")) {
            throw new IllegalArgumentException("Tipo de usuario invÃ¡lido: " + tipo);
        }

        return normalizedTipo;
    }

    private void createProfileByTipo(Usuario usuario) {
        switch (usuario.getTipo()) {
            case "voluntario":
                Voluntario voluntario = new Voluntario();
                voluntario.setNombre(usuario.getNombre());
                voluntario.setEmail(usuario.getEmail());
                voluntarioRepository.save(voluntario);
                break;
            case "beneficiario":
                Beneficiario beneficiario = new Beneficiario();
                beneficiario.setNombre(usuario.getNombre());
                beneficiario.setContacto(usuario.getEmail());
                beneficiarioRepository.save(beneficiario);
                break;
            case "organizacion":
                Organizacion organizacion = new Organizacion();
                organizacion.setNombre(usuario.getNombre());
                organizacion.setEmail(usuario.getEmail());
                organizacionRepository.save(organizacion);
                break;
            default:
                throw new IllegalArgumentException("Tipo de usuario invÃ¡lido: " + usuario.getTipo());
        }
    }
}
