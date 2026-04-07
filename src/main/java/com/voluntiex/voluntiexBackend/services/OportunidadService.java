package com.voluntiex.voluntiexBackend.services;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voluntiex.voluntiexBackend.models.Beneficiario;
import com.voluntiex.voluntiexBackend.models.EstadoOportunidad;
import com.voluntiex.voluntiexBackend.models.EstadoSolicitud;
import com.voluntiex.voluntiexBackend.models.Oportunidad;
import com.voluntiex.voluntiexBackend.models.Organizacion;
import com.voluntiex.voluntiexBackend.models.SolicitudVoluntariado;
import com.voluntiex.voluntiexBackend.models.Usuario;
import com.voluntiex.voluntiexBackend.repositories.BeneficiarioRepository;
import com.voluntiex.voluntiexBackend.repositories.OportunidadRepository;
import com.voluntiex.voluntiexBackend.repositories.OrganizacionRepository;
import com.voluntiex.voluntiexBackend.repositories.SolicitudVoluntariadoRepository;
import com.voluntiex.voluntiexBackend.utils.OportunidadSpecification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OportunidadService {

    @Autowired
    private OportunidadRepository oportunidadRepository;

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @Autowired
    private SolicitudVoluntariadoRepository solicitudVoluntariadoRepository;

    @Autowired
    private AuthService authService;

    @Transactional
    public Oportunidad createOportunidad(Oportunidad oportunidad, Authentication authentication) {
        Usuario usuario = getAuthenticatedUser(authentication);
        String tipoUsuario = normalizeTipoUsuario(usuario.getTipo());

        if ("voluntario".equals(tipoUsuario)) {
            throw new AccessDeniedException("Los voluntarios no pueden crear oportunidades");
        }

        oportunidad.setEstado(EstadoOportunidad.ACTIVA);

        if ("organizacion".equals(tipoUsuario)) {
            Organizacion organizacion = resolveOrCreateOrganizacion(usuario);
            oportunidad.setOrganizacion(organizacion);
            oportunidad.setBeneficiarioCreador(null);
            oportunidad.setTipoCreador("organizacion");
            oportunidad.setNombreUsuario(organizacion.getNombre());
            oportunidad.setBeneficiarios(resolveBeneficiarios(oportunidad.getBeneficiarios(), null));
            return oportunidadRepository.save(oportunidad);
        }

        if ("beneficiario".equals(tipoUsuario)) {
            Beneficiario beneficiario = resolveOrCreateBeneficiario(usuario);
            oportunidad.setOrganizacion(null);
            oportunidad.setBeneficiarioCreador(beneficiario);
            oportunidad.setTipoCreador("beneficiario");
            oportunidad.setNombreUsuario(beneficiario.getNombre());
            oportunidad.setBeneficiarios(resolveBeneficiarios(oportunidad.getBeneficiarios(), beneficiario));
            return oportunidadRepository.save(oportunidad);
        }

        throw new AccessDeniedException("Solo beneficiarios y organizaciones pueden crear oportunidades");
    }

    public List<Oportunidad> getAllOportunidades() {
        return oportunidadRepository.findByEstadoOrderByFechaInicioAsc(EstadoOportunidad.ACTIVA);
    }

    public Oportunidad getOportunidadById(Long id) {
        return oportunidadRepository.findById(id).orElseThrow();
    }

    public List<Oportunidad> getMyOportunidades(Authentication authentication) {
        Usuario usuario = getAuthenticatedUser(authentication);
        String tipoUsuario = normalizeTipoUsuario(usuario.getTipo());

        List<Oportunidad> oportunidades;
        if ("organizacion".equals(tipoUsuario)) {
            Organizacion organizacion = resolveOrCreateOrganizacion(usuario);
            oportunidades = oportunidadRepository.findByOrganizacionIdAndEstadoOrderByFechaInicioDesc(
                    organizacion.getId(),
                    EstadoOportunidad.ACTIVA);
        } else if ("beneficiario".equals(tipoUsuario)) {
            Beneficiario beneficiario = resolveOrCreateBeneficiario(usuario);
            oportunidades = oportunidadRepository.findByBeneficiarioCreadorIdAndEstadoOrderByFechaInicioDesc(
                    beneficiario.getId(),
                    EstadoOportunidad.ACTIVA);
        } else {
            throw new AccessDeniedException("Solo organizaciones y beneficiarios tienen oportunidades creadas");
        }

        oportunidades.forEach(this::attachSolicitudSummary);
        return oportunidades;
    }

    @Transactional
    public Oportunidad updateOportunidad(Long id, Oportunidad oportunidad, Authentication authentication) {
        Usuario usuario = getAuthenticatedUser(authentication);
        Oportunidad existingOportunidad = oportunidadRepository.findById(id).orElseThrow();
        validateOpportunityOwnership(existingOportunidad, usuario);
        ensureOportunidadActiva(existingOportunidad);

        existingOportunidad.setTitulo(oportunidad.getTitulo());
        existingOportunidad.setDescripcion(oportunidad.getDescripcion());
        existingOportunidad.setUbicacion(oportunidad.getUbicacion());
        existingOportunidad.setCategoria(oportunidad.getCategoria());
        existingOportunidad.setFechaInicio(oportunidad.getFechaInicio());
        existingOportunidad.setFechaFin(oportunidad.getFechaFin());
        existingOportunidad.setDuracion(oportunidad.getDuracion());
        existingOportunidad.setTipo(oportunidad.getTipo());
        existingOportunidad.setRequisitos(oportunidad.getRequisitos());

        if ("organizacion".equals(existingOportunidad.getTipoCreador())) {
            existingOportunidad.setBeneficiarios(resolveBeneficiarios(oportunidad.getBeneficiarios(), null));
        } else {
            existingOportunidad.setBeneficiarios(
                    resolveBeneficiarios(oportunidad.getBeneficiarios(), existingOportunidad.getBeneficiarioCreador()));
        }

        return oportunidadRepository.save(existingOportunidad);
    }

    @Transactional
    public void deleteOportunidad(Long id, Authentication authentication) {
        Usuario usuario = getAuthenticatedUser(authentication);
        Oportunidad oportunidad = oportunidadRepository.findById(id).orElseThrow();
        validateOpportunityOwnership(oportunidad, usuario);

        if (oportunidad.getEstado() == EstadoOportunidad.CANCELADA) {
            throw new IllegalArgumentException("La oportunidad ya estaba cancelada");
        }

        oportunidad.setEstado(EstadoOportunidad.CANCELADA);
        oportunidadRepository.save(oportunidad);
        cancelSolicitudesByOpportunity(oportunidad);
    }

    public List<Oportunidad> filtrarOportunidades(String titulo, String categoria, String ubicacion,
            LocalDate fechaInicio, LocalDate fechaFin,
            Integer duracion, String tipo, String requisitos) {

        Specification<Oportunidad> specification = Specification.where(OportunidadSpecification.filterByEstadoActiva());

        if (titulo != null && !titulo.isEmpty()) {
            specification = specification.and(OportunidadSpecification.filterByTitulo(titulo));
        }
        if (categoria != null && !categoria.isEmpty()) {
            specification = specification.and(OportunidadSpecification.filterByCategoria(categoria));
        }
        if (ubicacion != null && !ubicacion.isEmpty()) {
            specification = specification.and(OportunidadSpecification.filterByUbicacion(ubicacion));
        }
        if (fechaInicio != null) {
            specification = specification.and(OportunidadSpecification.filterByFechaInicio(fechaInicio));
        }
        if (fechaFin != null) {
            specification = specification.and(OportunidadSpecification.filterByFechaFin(fechaFin));
        }
        if (duracion != null) {
            specification = specification.and(OportunidadSpecification.filterByDuracion(duracion));
        }
        if (tipo != null && !tipo.isEmpty()) {
            specification = specification.and(OportunidadSpecification.filterByTipo(tipo));
        }
        if (requisitos != null && !requisitos.isEmpty()) {
            specification = specification.and(OportunidadSpecification.filterByRequisitos(requisitos));
        }

        return oportunidadRepository.findAll(specification);
    }

    private Usuario getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AccessDeniedException("Debes iniciar sesion para gestionar oportunidades");
        }
        return authService.getUserByEmail(authentication.getName());
    }

    private String normalizeTipoUsuario(String tipoUsuario) {
        if (tipoUsuario == null || tipoUsuario.isBlank()) {
            throw new AccessDeniedException("El usuario autenticado no tiene tipo configurado");
        }

        return Normalizer.normalize(tipoUsuario.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private Organizacion resolveOrCreateOrganizacion(Usuario usuario) {
        return organizacionRepository.findByUsuarioEmailIgnoreCase(usuario.getEmail())
                .or(() -> organizacionRepository.findByEmailIgnoreCase(usuario.getEmail())
                        .map(organizacion -> {
                            organizacion.setUsuario(usuario);
                            return organizacionRepository.save(organizacion);
                        }))
                .orElseGet(() -> {
                    Organizacion organizacion = new Organizacion();
                    organizacion.setNombre(usuario.getNombre());
                    organizacion.setEmail(usuario.getEmail());
                    organizacion.setUsuario(usuario);
                    return organizacionRepository.save(organizacion);
                });
    }

    private Beneficiario resolveOrCreateBeneficiario(Usuario usuario) {
        return beneficiarioRepository.findByUsuarioEmailIgnoreCase(usuario.getEmail())
                .or(() -> beneficiarioRepository.findByContactoIgnoreCase(usuario.getEmail())
                        .map(beneficiario -> {
                            beneficiario.setUsuario(usuario);
                            if (beneficiario.getNombre() == null || beneficiario.getNombre().isBlank()) {
                                beneficiario.setNombre(usuario.getNombre());
                            }
                            return beneficiarioRepository.save(beneficiario);
                        }))
                .orElseGet(() -> {
                    Beneficiario beneficiario = new Beneficiario();
                    beneficiario.setNombre(usuario.getNombre());
                    beneficiario.setContacto(usuario.getEmail());
                    beneficiario.setUsuario(usuario);
                    return beneficiarioRepository.save(beneficiario);
                });
    }

    private List<Beneficiario> resolveBeneficiarios(List<Beneficiario> requestedBeneficiarios, Beneficiario beneficiarioCreador) {
        Set<Long> beneficiarioIds = new LinkedHashSet<>();

        if (requestedBeneficiarios != null) {
            for (Beneficiario beneficiario : requestedBeneficiarios) {
                if (beneficiario.getId() == null) {
                    throw new IllegalArgumentException("Cada beneficiario debe enviarse con id");
                }
                beneficiarioIds.add(beneficiario.getId());
            }
        }

        if (beneficiarioCreador != null && beneficiarioCreador.getId() != null) {
            beneficiarioIds.add(beneficiarioCreador.getId());
        }

        if (beneficiarioIds.isEmpty()) {
            throw new IllegalArgumentException("La oportunidad debe tener al menos un beneficiario");
        }

        List<Beneficiario> beneficiarios = new ArrayList<>(beneficiarioRepository.findAllById(beneficiarioIds));
        if (beneficiarios.size() != beneficiarioIds.size()) {
            throw new IllegalArgumentException("Uno o mas beneficiarios no existen");
        }

        return beneficiarios;
    }

    private void attachSolicitudSummary(Oportunidad oportunidad) {
        long totalSolicitudes = solicitudVoluntariadoRepository.countByOportunidadIdAndEstadoNot(
                oportunidad.getId(),
                EstadoSolicitud.CANCELADA);
        oportunidad.setTotalSolicitudes(totalSolicitudes);
        oportunidad.setTieneSolicitudes(totalSolicitudes > 0);
    }

    private void validateOpportunityOwnership(Oportunidad oportunidad, Usuario usuario) {
        String tipoUsuario = normalizeTipoUsuario(usuario.getTipo());

        if ("organizacion".equals(tipoUsuario)) {
            Organizacion organizacion = oportunidad.getOrganizacion();
            if (organizacion == null) {
                throw new AccessDeniedException("Esta oportunidad no fue creada por una organizacion");
            }
            if (matchesUsuario(organizacion.getUsuario(), usuario.getEmail())
                    || (organizacion.getEmail() != null && organizacion.getEmail().equalsIgnoreCase(usuario.getEmail()))) {
                return;
            }
            throw new AccessDeniedException("No puedes gestionar una oportunidad ajena");
        }

        if ("beneficiario".equals(tipoUsuario)) {
            Beneficiario beneficiario = oportunidad.getBeneficiarioCreador();
            if (beneficiario == null) {
                throw new AccessDeniedException("Esta oportunidad no fue creada por un beneficiario");
            }
            if (matchesUsuario(beneficiario.getUsuario(), usuario.getEmail())
                    || (beneficiario.getContacto() != null && beneficiario.getContacto().equalsIgnoreCase(usuario.getEmail()))) {
                return;
            }
            throw new AccessDeniedException("No puedes gestionar una oportunidad ajena");
        }

        throw new AccessDeniedException("Solo organizaciones y beneficiarios pueden gestionar oportunidades");
    }

    private boolean matchesUsuario(Usuario usuario, String email) {
        return usuario != null && usuario.getEmail() != null && usuario.getEmail().equalsIgnoreCase(email);
    }

    private void ensureOportunidadActiva(Oportunidad oportunidad) {
        if (oportunidad.getEstado() != EstadoOportunidad.ACTIVA) {
            throw new IllegalArgumentException("La oportunidad ya no esta activa");
        }
    }

    private void cancelSolicitudesByOpportunity(Oportunidad oportunidad) {
        List<SolicitudVoluntariado> solicitudes = solicitudVoluntariadoRepository
                .findByOportunidadIdOrderByFechaSolicitudDesc(oportunidad.getId());

        for (SolicitudVoluntariado solicitud : solicitudes) {
            solicitud.setEstado(EstadoSolicitud.CANCELADA);
            solicitud.setFechaActualizacion(LocalDateTime.now());
        }

        solicitudVoluntariadoRepository.saveAll(solicitudes);
    }
}
