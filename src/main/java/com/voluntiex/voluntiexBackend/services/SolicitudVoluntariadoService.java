package com.voluntiex.voluntiexBackend.services;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voluntiex.voluntiexBackend.models.EstadoSolicitud;
import com.voluntiex.voluntiexBackend.models.EstadoOportunidad;
import com.voluntiex.voluntiexBackend.models.Beneficiario;
import com.voluntiex.voluntiexBackend.models.Oportunidad;
import com.voluntiex.voluntiexBackend.models.Organizacion;
import com.voluntiex.voluntiexBackend.models.SolicitudVoluntariado;
import com.voluntiex.voluntiexBackend.models.Usuario;
import com.voluntiex.voluntiexBackend.models.Voluntario;
import com.voluntiex.voluntiexBackend.repositories.OportunidadRepository;
import com.voluntiex.voluntiexBackend.repositories.SolicitudVoluntariadoRepository;
import com.voluntiex.voluntiexBackend.repositories.VoluntarioRepository;

@Service
public class SolicitudVoluntariadoService {

    @Autowired
    private SolicitudVoluntariadoRepository solicitudVoluntariadoRepository;

    @Autowired
    private OportunidadRepository oportunidadRepository;

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private AuthService authService;

    @Transactional
    public SolicitudVoluntariado postularse(Long oportunidadId, Authentication authentication) {
        Usuario usuario = getAuthenticatedUser(authentication);
        if (!"voluntario".equals(normalizeTipoUsuario(usuario.getTipo()))) {
            throw new AccessDeniedException("Solo los voluntarios pueden postularse");
        }

        Voluntario voluntario = resolveOrCreateVoluntario(usuario);
        Oportunidad oportunidad = getOportunidad(oportunidadId);
        ensureOportunidadActiva(oportunidad);

        SolicitudVoluntariado solicitudExistente = solicitudVoluntariadoRepository
                .findByOportunidadIdAndVoluntarioId(oportunidadId, voluntario.getId())
                .orElse(null);

        if (solicitudExistente != null) {
            if (solicitudExistente.getEstado() == EstadoSolicitud.CANCELADA
                    || solicitudExistente.getEstado() == EstadoSolicitud.RECHAZADA) {
                solicitudExistente.setEstado(EstadoSolicitud.PENDIENTE);
                solicitudExistente.setFechaSolicitud(LocalDateTime.now());
                solicitudExistente.setFechaActualizacion(LocalDateTime.now());
                return solicitudVoluntariadoRepository.save(solicitudExistente);
            }
            throw new IllegalArgumentException("Ya existe una solicitud activa para esta oportunidad");
        }

        SolicitudVoluntariado solicitud = new SolicitudVoluntariado();
        solicitud.setOportunidad(oportunidad);
        solicitud.setVoluntario(voluntario);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setFechaActualizacion(LocalDateTime.now());
        return solicitudVoluntariadoRepository.save(solicitud);
    }

    public List<SolicitudVoluntariado> getSolicitudesPorOportunidad(Long oportunidadId, Authentication authentication) {
        Usuario usuario = getAuthenticatedUser(authentication);
        Oportunidad oportunidad = getOportunidad(oportunidadId);
        validateOpportunityOwnership(oportunidad, usuario);
        return solicitudVoluntariadoRepository.findByOportunidadIdOrderByFechaSolicitudDesc(oportunidadId);
    }

    @Transactional
    public SolicitudVoluntariado actualizarEstado(Long solicitudId, String estado, Authentication authentication) {
        Usuario usuario = getAuthenticatedUser(authentication);
        SolicitudVoluntariado solicitud = solicitudVoluntariadoRepository.findById(solicitudId).orElseThrow();
        ensureOportunidadActiva(solicitud.getOportunidad());
        validateOpportunityOwnership(solicitud.getOportunidad(), usuario);

        if (solicitud.getEstado() == EstadoSolicitud.CANCELADA) {
            throw new IllegalArgumentException("No se puede actualizar una solicitud cancelada");
        }

        solicitud.setEstado(parseEstado(estado));
        solicitud.setFechaActualizacion(LocalDateTime.now());
        return solicitudVoluntariadoRepository.save(solicitud);
    }

    @Transactional
    public SolicitudVoluntariado cancelarPostulacion(Long solicitudId, Authentication authentication) {
        Usuario usuario = getAuthenticatedUser(authentication);
        if (!"voluntario".equals(normalizeTipoUsuario(usuario.getTipo()))) {
            throw new AccessDeniedException("Solo los voluntarios pueden cancelar sus postulaciones");
        }

        Voluntario voluntario = resolveOrCreateVoluntario(usuario);
        SolicitudVoluntariado solicitud = solicitudVoluntariadoRepository.findById(solicitudId).orElseThrow();

        if (!solicitud.getVoluntario().getId().equals(voluntario.getId())) {
            throw new AccessDeniedException("No puedes cancelar la postulacion de otro voluntario");
        }
        if (solicitud.getEstado() == EstadoSolicitud.CANCELADA) {
            throw new IllegalArgumentException("La solicitud ya estaba cancelada");
        }

        solicitud.setEstado(EstadoSolicitud.CANCELADA);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        return solicitudVoluntariadoRepository.save(solicitud);
    }

    public List<SolicitudVoluntariado> getSolicitudesDelVoluntario(Authentication authentication) {
        Usuario usuario = getAuthenticatedUser(authentication);
        if (!"voluntario".equals(normalizeTipoUsuario(usuario.getTipo()))) {
            throw new AccessDeniedException("Solo los voluntarios pueden consultar sus solicitudes");
        }

        Voluntario voluntario = resolveOrCreateVoluntario(usuario);
        return solicitudVoluntariadoRepository.findByVoluntarioIdOrderByFechaSolicitudDesc(voluntario.getId());
    }

    private Usuario getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AccessDeniedException("Debes iniciar sesion para continuar");
        }
        return authService.getUserByEmail(authentication.getName());
    }

    private Oportunidad getOportunidad(Long oportunidadId) {
        return oportunidadRepository.findById(oportunidadId).orElseThrow();
    }

    private void ensureOportunidadActiva(Oportunidad oportunidad) {
        if (oportunidad.getEstado() != EstadoOportunidad.ACTIVA) {
            throw new IllegalArgumentException("La oportunidad ya no esta disponible");
        }
    }

    private Voluntario resolveOrCreateVoluntario(Usuario usuario) {
        return voluntarioRepository.findByUsuarioEmailIgnoreCase(usuario.getEmail())
                .or(() -> voluntarioRepository.findByEmailIgnoreCase(usuario.getEmail())
                        .map(voluntario -> {
                            voluntario.setUsuario(usuario);
                            if (voluntario.getNombre() == null || voluntario.getNombre().isBlank()) {
                                voluntario.setNombre(usuario.getNombre());
                            }
                            return voluntarioRepository.save(voluntario);
                        }))
                .orElseGet(() -> {
                    Voluntario voluntario = new Voluntario();
                    voluntario.setNombre(usuario.getNombre());
                    voluntario.setEmail(usuario.getEmail());
                    voluntario.setUsuario(usuario);
                    return voluntarioRepository.save(voluntario);
                });
    }

    private void validateOpportunityOwnership(Oportunidad oportunidad, Usuario usuario) {
        String tipoUsuario = normalizeTipoUsuario(usuario.getTipo());

        if ("organizacion".equals(tipoUsuario)) {
            Organizacion organizacion = oportunidad.getOrganizacion();
            if (organizacion == null) {
                throw new AccessDeniedException("Esta oportunidad no fue creada por una organizacion");
            }
            if (matchesUsuario(organizacion.getUsuario(), usuario.getEmail())) {
                return;
            }
            if (organizacion.getEmail() != null && organizacion.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                return;
            }
            throw new AccessDeniedException("No puedes gestionar solicitudes de una oportunidad ajena");
        }

        if ("beneficiario".equals(tipoUsuario)) {
            Beneficiario beneficiario = oportunidad.getBeneficiarioCreador();
            if (beneficiario == null) {
                throw new AccessDeniedException("Esta oportunidad no tiene un beneficiario creador asociado");
            }
            if (matchesUsuario(beneficiario.getUsuario(), usuario.getEmail())) {
                return;
            }
            if (beneficiario.getContacto() != null && beneficiario.getContacto().equalsIgnoreCase(usuario.getEmail())) {
                return;
            }
            throw new AccessDeniedException("No puedes gestionar solicitudes de una oportunidad ajena");
        }

        throw new AccessDeniedException("Solo organizaciones y beneficiarios pueden revisar solicitudes");
    }

    private boolean matchesUsuario(Usuario usuario, String email) {
        return usuario != null && usuario.getEmail() != null && usuario.getEmail().equalsIgnoreCase(email);
    }

    private EstadoSolicitud parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        String normalizedEstado = Normalizer.normalize(estado.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);

        return switch (normalizedEstado) {
            case "PENDIENTE" -> EstadoSolicitud.PENDIENTE;
            case "APROBADA", "APROBADO", "SELECCIONADA", "SELECCIONADO" -> EstadoSolicitud.APROBADA;
            case "RECHAZADA", "RECHAZADO" -> EstadoSolicitud.RECHAZADA;
            case "CANCELADA", "CANCELADO" -> EstadoSolicitud.CANCELADA;
            default -> throw new IllegalArgumentException("Estado invalido: " + estado);
        };
    }

    private String normalizeTipoUsuario(String tipoUsuario) {
        if (tipoUsuario == null || tipoUsuario.isBlank()) {
            throw new AccessDeniedException("El usuario autenticado no tiene tipo configurado");
        }

        return Normalizer.normalize(tipoUsuario.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
