package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.ActuacionAdministrativa;
import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.OrganoAdministracion;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.ReformaEstatutaria;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.BloqueoDto;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto.CampoPreviewDto;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto.SeccionPreviewDto;
import co.gov.bogota.sed.esal.repository.ActuacionAdministrativaRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.NombramientoRepository;
import co.gov.bogota.sed.esal.repository.OrganoAdministracionRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
import co.gov.bogota.sed.esal.repository.ReformaEstatutariaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PreviewService {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EsalRepository esalRepository;
    private final PersoneriaJuridicaRepository personeriaRepository;
    private final ReformaEstatutariaRepository reformaRepository;
    private final NombramientoRepository nombramientoRepository;
    private final OrganoAdministracionRepository organoRepository;
    private final ActuacionAdministrativaRepository actuacionRepository;
    private final AuditoriaService auditoriaService;

    public PreviewService(
            EsalRepository esalRepository,
            PersoneriaJuridicaRepository personeriaRepository,
            ReformaEstatutariaRepository reformaRepository,
            NombramientoRepository nombramientoRepository,
            OrganoAdministracionRepository organoRepository,
            ActuacionAdministrativaRepository actuacionRepository,
            AuditoriaService auditoriaService) {
        this.esalRepository = esalRepository;
        this.personeriaRepository = personeriaRepository;
        this.reformaRepository = reformaRepository;
        this.nombramientoRepository = nombramientoRepository;
        this.organoRepository = organoRepository;
        this.actuacionRepository = actuacionRepository;
        this.auditoriaService = auditoriaService;
    }

    // -------------------------------------------------------------------------
    // Preview del certificado
    // -------------------------------------------------------------------------

    public PreviewCertificadoDto obtenerPreview(Long esalId, Authentication auth) {
        Esal esal = obtenerEsal(esalId);

        List<SeccionPreviewDto> secciones = construirSecciones(esal);
        List<String> advertencias = construirAdvertencias(esal);
        List<BloqueoDto> bloqueos = construirBloqueos(secciones);

        boolean habilitada = bloqueos.isEmpty()
                && !EstadoEsal.CANCELADO.equals(esal.getEstado())
                && EstadoCompletitud.LISTO_PARA_CERTIFICAR.equals(esal.getEstadoCompletitud());

        PreviewCertificadoDto dto = new PreviewCertificadoDto();
        dto.setEsalId(esal.getId());
        dto.setIdSipej(esal.getIdSipej());
        dto.setNit(esal.getNit());
        dto.setNombre(esal.getNombre());
        dto.setEstado(esal.getEstado());
        dto.setEstadoCompletitud(esal.getEstadoCompletitud());
        dto.setVersionDatos(esal.getUpdatedAt());
        dto.setGeneracionHabilitada(habilitada);
        dto.setAlertaEstado(resolverAlertaEstado(esal.getEstado()));
        dto.setSecciones(secciones);
        dto.setAdvertencias(advertencias);
        dto.setBloqueos(bloqueos);

        String usuario = auth != null ? auth.getName() : "sistema";
        String accion = bloqueos.isEmpty()
                ? AuditoriaAcciones.PREVIEW_CERTIFICADO_CONSULTADO
                : AuditoriaAcciones.PREVIEW_CERTIFICADO_BLOQUEADO;
        auditoriaService.registrar(
                usuario, auditoriaService.obtenerRolActual(),
                accion, AuditoriaAcciones.ENTIDAD_ESAL,
                esalId, esal.getIdSipej(), AuditoriaAcciones.RESULTADO_EXITO,
                "Bloqueos: " + bloqueos.size() + ", Habilitada: " + habilitada);

        return dto;
    }

    // -------------------------------------------------------------------------
    // Validación explícita (POST)
    // -------------------------------------------------------------------------

    public PreviewCertificadoDto validar(Long esalId, Authentication auth) {
        PreviewCertificadoDto preview = obtenerPreview(esalId, auth);

        if (!Boolean.TRUE.equals(preview.getGeneracionHabilitada())) {
            String usuario = auth != null ? auth.getName() : "sistema";
            auditoriaService.registrar(
                    usuario, auditoriaService.obtenerRolActual(),
                    AuditoriaAcciones.ERROR_VALIDACION_PREVIEW, AuditoriaAcciones.ENTIDAD_ESAL,
                    esalId, preview.getIdSipej(), AuditoriaAcciones.RESULTADO_ERROR,
                    "Bloqueos: " + preview.getBloqueos().size());
        }

        return preview;
    }

    // -------------------------------------------------------------------------
    // Construcción de secciones del certificado
    // -------------------------------------------------------------------------

    private List<SeccionPreviewDto> construirSecciones(Esal esal) {
        Long esalId = esal.getId();
        List<SeccionPreviewDto> secciones = new ArrayList<>();

        // Sección: Información Principal
        List<CampoPreviewDto> camposInfo = new ArrayList<>();
        camposInfo.add(campo("NOMBRE", esal.getNombre(), true));
        camposInfo.add(campo("NIT", esal.getNit(), false));
        camposInfo.add(campo("ID SIPEJ", esal.getIdSipej(), true));
        camposInfo.add(campo("DOMICILIO", esal.getDomicilio(), true));
        camposInfo.add(campo("CORREO ELECTRONICO", esal.getCorreoElectronico(), true));
        camposInfo.add(campo("TERMINO DE DURACION", esal.getTerminoDuracion(), true));
        camposInfo.add(campo("OBJETO SOCIAL", esal.getObjetoSocial(), true));
        secciones.add(new SeccionPreviewDto("INFORMACION PRINCIPAL", camposInfo));

        // Sección: Constitución y Personería
        List<PersoneriaJuridica> pjs = personeriaRepository.findByEsalId(esalId);
        PersoneriaJuridica pj = pjs.isEmpty() ? null : pjs.get(0);
        List<CampoPreviewDto> camposPersoneria = new ArrayList<>();
        camposPersoneria.add(campo("RECONOCIMIENTO DE PERSONERIA JURIDICA",
                pj != null ? pj.getReconocimientoPersoneriaJuridica() : null, true));
        camposPersoneria.add(campo("FECHA RECONOCIMIENTO",
                pj != null ? formatFecha(pj.getFechaReconocimientoPersoneriaJuridica()) : null, true));
        camposPersoneria.add(campo("ENTIDAD QUE EXPIDE",
                pj != null ? pj.getEntidadQueExpide() : null, true));
        camposPersoneria.add(campo("INSCRIPCION",
                pj != null ? pj.getInscripcion() : null, false));
        camposPersoneria.add(campo("FECHA INSCRIPCION",
                pj != null ? formatFecha(pj.getFechaInscripcion()) : null, false));
        camposPersoneria.add(campo("ENTIDAD QUE INSCRIBIO",
                pj != null ? pj.getEntidadQueInscribio() : null, false));
        secciones.add(new SeccionPreviewDto("CONSTITUCION Y PERSONERIA JURIDICA", camposPersoneria));

        // Sección: Reformas
        List<ReformaEstatutaria> reformas = reformaRepository.findByEsalIdOrderByOrden(esalId);
        if (!reformas.isEmpty()) {
            List<CampoPreviewDto> camposReformas = new ArrayList<>();
            for (ReformaEstatutaria r : reformas) {
                String etiqueta = "REFORMA " + (r.getOrden() != null ? r.getOrden() : "");
                String valor = combinar(r.getTipoActo(), r.getNumeroActo(),
                        formatFecha(r.getFechaActo()), r.getEntidadQueExpide());
                camposReformas.add(new CampoPreviewDto(etiqueta, valor, false, false, false));
            }
            secciones.add(new SeccionPreviewDto("REFORMAS ESTATUTARIAS", camposReformas));
        }

        // Sección: Nombramientos
        List<Nombramiento> nombramientos = nombramientoRepository.findByEsalId(esalId);
        if (!nombramientos.isEmpty()) {
            List<CampoPreviewDto> camposNomb = new ArrayList<>();
            for (Nombramiento n : nombramientos) {
                String tipo = n.getTipoNombramiento() != null ? n.getTipoNombramiento().name() : "";
                boolean obligatorio = "REPRESENTANTE_LEGAL".equals(tipo);
                camposNomb.add(campo(tipo + " - NOMBRE", n.getNombre(), obligatorio));
                camposNomb.add(campo(tipo + " - DOCUMENTO",
                        combinar(n.getTipoDocumento(), n.getNumeroDocumento()), obligatorio));
                if (n.getCargo() != null) {
                    camposNomb.add(new CampoPreviewDto(tipo + " - CARGO", n.getCargo(), false, false, false));
                }
                camposNomb.add(campo(tipo + " - ACTA APRUEBA", n.getActaAprueba(), obligatorio));
                camposNomb.add(campo(tipo + " - FECHA ACTA", formatFecha(n.getFechaActa()), obligatorio));
                if (n.getFacultadesLimitaciones() != null) {
                    camposNomb.add(campo(tipo + " - FACULTADES", n.getFacultadesLimitaciones(), obligatorio));
                }
            }
            secciones.add(new SeccionPreviewDto("NOMBRAMIENTOS", camposNomb));
        }

        // Sección: Órgano de Administración
        List<OrganoAdministracion> organos = organoRepository.findByEsalId(esalId);
        if (!organos.isEmpty()) {
            List<CampoPreviewDto> camposOrgano = new ArrayList<>();
            for (OrganoAdministracion o : organos) {
                String orgLabel = o.getOrgano() != null ? o.getOrgano() : "ORGANO";
                camposOrgano.add(new CampoPreviewDto(orgLabel + " - MIEMBRO", o.getMiembro(), false, false, false));
                if (o.getCargo() != null) {
                    camposOrgano.add(new CampoPreviewDto(orgLabel + " - CARGO", o.getCargo(), false, false, false));
                }
                if (o.getActaAprueba() != null) {
                    camposOrgano.add(new CampoPreviewDto(orgLabel + " - ACTA", o.getActaAprueba(), false, false, false));
                }
            }
            secciones.add(new SeccionPreviewDto("ORGANO DE ADMINISTRACION", camposOrgano));
        }

        // Sección: Actuaciones Administrativas
        List<ActuacionAdministrativa> actuaciones = actuacionRepository.findByEsalId(esalId);
        if (!actuaciones.isEmpty()) {
            List<CampoPreviewDto> camposAct = new ArrayList<>();
            for (ActuacionAdministrativa a : actuaciones) {
                String tipo = a.getTipoActuacion() != null ? a.getTipoActuacion().name() : "ACTUACION";
                if (a.getActa() != null) {
                    camposAct.add(new CampoPreviewDto(tipo + " - ACTA", a.getActa(), false, false, false));
                }
                if (a.getFechaActa() != null) {
                    camposAct.add(new CampoPreviewDto(tipo + " - FECHA ACTA", formatFecha(a.getFechaActa()), false, false, false));
                }
                if (a.getResolucion() != null) {
                    camposAct.add(new CampoPreviewDto(tipo + " - RESOLUCION", a.getResolucion(), false, false, false));
                }
                if (a.getMotivo() != null) {
                    camposAct.add(new CampoPreviewDto(tipo + " - MOTIVO", a.getMotivo(), false, false, false));
                }
            }
            secciones.add(new SeccionPreviewDto("ACTUACIONES ADMINISTRATIVAS", camposAct));
        }

        return secciones;
    }

    // -------------------------------------------------------------------------
    // Bloqueos: campos obligatorios faltantes
    // -------------------------------------------------------------------------

    private List<BloqueoDto> construirBloqueos(List<SeccionPreviewDto> secciones) {
        List<BloqueoDto> bloqueos = new ArrayList<>();
        for (SeccionPreviewDto seccion : secciones) {
            for (CampoPreviewDto campo : seccion.getCampos()) {
                if (Boolean.TRUE.equals(campo.getFaltante()) && Boolean.TRUE.equals(campo.getObligatorio())) {
                    bloqueos.add(new BloqueoDto(
                            seccion.getNombre(),
                            campo.getEtiqueta(),
                            "CAMPO_OBLIGATORIO_FALTANTE",
                            "El campo '" + campo.getEtiqueta() + "' es obligatorio y está faltante.",
                            Boolean.TRUE.equals(campo.getOrigenHistorico())));
                }
            }
        }
        return bloqueos;
    }

    // -------------------------------------------------------------------------
    // Advertencias textuales por estado de la ESAL
    // -------------------------------------------------------------------------

    private List<String> construirAdvertencias(Esal esal) {
        List<String> advertencias = new ArrayList<>();
        if (EstadoEsal.SUSPENDIDO.equals(esal.getEstado())) {
            advertencias.add("La ESAL se encuentra SUSPENDIDA. El certificado reflejará esta condición.");
        } else if (EstadoEsal.EN_LIQUIDACION.equals(esal.getEstado())) {
            advertencias.add("La ESAL se encuentra EN LIQUIDACION. Verifique los datos antes de certificar.");
        } else if (EstadoEsal.CANCELADO.equals(esal.getEstado())) {
            advertencias.add("La ESAL está CANCELADA. No se puede generar certificado para entidades canceladas.");
        }
        if (EstadoCompletitud.INCOMPLETO_BLOQUEANTE.equals(esal.getEstadoCompletitud())) {
            advertencias.add("Existen campos obligatorios faltantes que bloquean la generación del certificado.");
        } else if (EstadoCompletitud.INCOMPLETO_NO_BLOQUEANTE.equals(esal.getEstadoCompletitud())) {
            advertencias.add("Existen campos opcionales faltantes. El certificado puede generarse pero estará incompleto.");
        }
        return advertencias;
    }

    private String resolverAlertaEstado(EstadoEsal estado) {
        if (estado == null) return null;
        switch (estado) {
            case SUSPENDIDO:    return "ESAL SUSPENDIDA";
            case EN_LIQUIDACION: return "ESAL EN LIQUIDACION";
            case CANCELADO:     return "ESAL CANCELADA — generación no permitida";
            default:            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private CampoPreviewDto campo(String etiqueta, String valor, boolean obligatorio) {
        boolean faltante = valor == null || valor.trim().isEmpty();
        return new CampoPreviewDto(etiqueta, faltante ? null : valor, faltante, obligatorio, false);
    }

    private String formatFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(FECHA_FMT) : null;
    }

    private String combinar(String... partes) {
        StringBuilder sb = new StringBuilder();
        for (String p : partes) {
            if (p != null && !p.trim().isEmpty()) {
                if (sb.length() > 0) sb.append(" | ");
                sb.append(p.trim());
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private Esal obtenerEsal(Long esalId) {
        return esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));
    }
}
