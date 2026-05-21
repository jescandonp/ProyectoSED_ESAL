package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.ActuacionAdministrativa;
import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.OrganoAdministracion;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoActuacion;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.BloqueoDto;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto.CampoPreviewDto;
import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto.SeccionPreviewDto;
import co.gov.bogota.sed.esal.repository.ActuacionAdministrativaRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.NombramientoRepository;
import co.gov.bogota.sed.esal.repository.OrganoAdministracionRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PreviewService {

    private static final String TIPO_CAMPO_FALTANTE = "CAMPO_FALTANTE";
    private static final String TIPO_REGLA_ESTADO   = "REGLA_ESTADO";

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EsalRepository esalRepository;
    private final PersoneriaJuridicaRepository personeriaRepository;
    private final NombramientoRepository nombramientoRepository;
    private final OrganoAdministracionRepository organoRepository;
    private final ActuacionAdministrativaRepository actuacionRepository;
    private final AuditoriaService auditoriaService;

    public PreviewService(EsalRepository esalRepository,
                          PersoneriaJuridicaRepository personeriaRepository,
                          NombramientoRepository nombramientoRepository,
                          OrganoAdministracionRepository organoRepository,
                          ActuacionAdministrativaRepository actuacionRepository,
                          AuditoriaService auditoriaService) {
        this.esalRepository = esalRepository;
        this.personeriaRepository = personeriaRepository;
        this.nombramientoRepository = nombramientoRepository;
        this.organoRepository = organoRepository;
        this.actuacionRepository = actuacionRepository;
        this.auditoriaService = auditoriaService;
    }

    public PreviewCertificadoDto obtenerPreview(Long esalId, String usuario) {
        Esal esal = esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));

        List<PersoneriaJuridica> personerias = personeriaRepository.findByEsalId(esalId);
        List<Nombramiento> nombramientos     = nombramientoRepository.findByEsalId(esalId);
        List<OrganoAdministracion> organos   = organoRepository.findByEsalId(esalId);
        List<ActuacionAdministrativa> actuaciones = actuacionRepository.findByEsalId(esalId);

        PersoneriaJuridica pj = personerias.isEmpty() ? null : personerias.get(0);

        List<BloqueoDto> bloqueos      = new ArrayList<>();
        List<String>     advertencias  = new ArrayList<>();

        // ── Campos base obligatorios ─────────────────────────────────────────
        verificarCampo(bloqueos, "INFORMACION PRINCIPAL", "NOMBRE",
                esal.getNombre(), true, "El nombre de la ESAL es obligatorio.");

        verificarCampo(bloqueos, "INFORMACION PRINCIPAL", "ID SIPEJ",
                esal.getIdSipej(), true, "El ID SIPEJ es obligatorio.");

        verificarCampo(bloqueos, "INFORMACION PRINCIPAL", "NIT",
                esal.getNit(), true, "El NIT es obligatorio.");

        verificarCampo(bloqueos, "INFORMACION PRINCIPAL", "DOMICILIO",
                esal.getDomicilio(), true, "El domicilio es obligatorio.");

        verificarCampo(bloqueos, "INFORMACION PRINCIPAL", "OBJETO SOCIAL",
                esal.getObjetoSocial(), true, "El objeto social es obligatorio.");

        verificarCampo(bloqueos, "INFORMACION PRINCIPAL", "TERMINO DE DURACION",
                esal.getTerminoDuracion(), true, "El término de duración es obligatorio.");

        // Personería jurídica
        if (pj != null) {
            verificarCampo(bloqueos, "CONSTITUCION Y REFORMAS", "RECONOCIMIENTO PERSONERIA JURIDICA",
                    pj.getReconocimientoPersoneriaJuridica(), true, "El acto de personería jurídica es obligatorio.");
            verificarCampo(bloqueos, "CONSTITUCION Y REFORMAS", "ENTIDAD QUE EXPIDE",
                    pj.getEntidadQueExpide(), true, "La entidad que expide la personería es obligatoria.");
        } else {
            bloqueos.add(new BloqueoDto("CONSTITUCION Y REFORMAS", "PERSONERIA JURIDICA",
                    TIPO_CAMPO_FALTANTE, "La personería jurídica es obligatoria.", false));
        }

        // Representante legal
        Nombramiento representante = nombramientos.stream()
                .filter(n -> TipoNombramiento.REPRESENTANTE_LEGAL.equals(n.getTipoNombramiento()))
                .filter(n -> Boolean.TRUE.equals(n.getVigente()))
                .findFirst().orElse(null);
        if (representante == null) {
            representante = nombramientos.stream()
                    .filter(n -> TipoNombramiento.REPRESENTANTE_LEGAL.equals(n.getTipoNombramiento()))
                    .findFirst().orElse(null);
        }
        if (representante != null) {
            verificarCampo(bloqueos, "NOMBRAMIENTOS", "REPRESENTANTE LEGAL - NOMBRE",
                    representante.getNombre(), true, "El nombre del representante legal es obligatorio.");
            verificarCampo(bloqueos, "NOMBRAMIENTOS", "REPRESENTANTE LEGAL - NUMERO DOCUMENTO",
                    representante.getNumeroDocumento(), true, "El número de documento del representante legal es obligatorio.");
        } else {
            bloqueos.add(new BloqueoDto("NOMBRAMIENTOS", "REPRESENTANTE LEGAL",
                    TIPO_CAMPO_FALTANTE, "El representante legal es obligatorio.", false));
        }

        // ── Reglas por estado ────────────────────────────────────────────────
        String alertaEstado = null;
        EstadoEsal estado = esal.getEstado();

        if (EstadoEsal.SUSPENDIDO.equals(estado)) {
            boolean tieneSuspension = actuaciones.stream()
                    .anyMatch(a -> TipoActuacion.SUSPENSION.equals(a.getTipoActuacion()));
            if (!tieneSuspension) {
                bloqueos.add(new BloqueoDto("ACTUACIONES ADMINISTRATIVAS", "ACTUACION SUSPENSION",
                        TIPO_REGLA_ESTADO,
                        "El estado SUSPENDIDO requiere al menos una actuación administrativa de tipo SUSPENSION.",
                        false));
            } else {
                alertaEstado = "ESAL en estado SUSPENDIDO. El certificado reflejará esta situación.";
            }
        } else if (EstadoEsal.EN_LIQUIDACION.equals(estado)) {
            boolean tieneLiquidacion = actuaciones.stream()
                    .anyMatch(a -> TipoActuacion.LIQUIDACION.equals(a.getTipoActuacion()));
            if (!tieneLiquidacion) {
                bloqueos.add(new BloqueoDto("ACTUACIONES ADMINISTRATIVAS", "ACTUACION LIQUIDACION",
                        TIPO_REGLA_ESTADO,
                        "El estado EN_LIQUIDACION requiere al menos una actuación administrativa de tipo LIQUIDACION.",
                        false));
            } else {
                alertaEstado = "ESAL en estado EN_LIQUIDACION. El certificado reflejará esta situación.";
            }
        } else if (EstadoEsal.CANCELADO.equals(estado)) {
            boolean tieneCancelacion = actuaciones.stream()
                    .anyMatch(a -> TipoActuacion.CANCELACION.equals(a.getTipoActuacion()));
            if (!tieneCancelacion) {
                bloqueos.add(new BloqueoDto("ACTUACIONES ADMINISTRATIVAS", "ACTUACION CANCELACION",
                        TIPO_REGLA_ESTADO,
                        "El estado CANCELADO requiere al menos una actuación administrativa de tipo CANCELACION.",
                        false));
            } else {
                alertaEstado = "ESAL en estado CANCELADO. El certificado reflejará esta situación.";
            }
        }

        // ── Campos opcionales (advertencias no bloqueantes) ──────────────────
        if (esFaltante(esal.getCorreoElectronico())) {
            advertencias.add("INFORMACION PRINCIPAL - CORREO ELECTRONICO: campo opcional faltante.");
        }

        // ── Secciones del certificado ────────────────────────────────────────
        List<SeccionPreviewDto> secciones = construirSecciones(esal, pj, representante, nombramientos, organos, actuaciones);

        // ── Construir DTO ────────────────────────────────────────────────────
        PreviewCertificadoDto dto = new PreviewCertificadoDto();
        dto.setEsalId(esal.getId());
        dto.setIdSipej(esal.getIdSipej());
        dto.setNit(esal.getNit());
        dto.setNombre(esal.getNombre());
        dto.setEstado(esal.getEstado());
        dto.setEstadoCompletitud(esal.getEstadoCompletitud());
        dto.setVersionDatos(esal.getUpdatedAt());
        dto.setBloqueos(bloqueos);
        dto.setAdvertencias(advertencias);
        dto.setGeneracionHabilitada(bloqueos.isEmpty());
        dto.setAlertaEstado(alertaEstado);
        dto.setSecciones(secciones);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.PREVIEW_CERTIFICADO_GENERADO,
                AuditoriaAcciones.ENTIDAD_ESAL,
                esal.getId(), esal.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO, null);

        return dto;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void verificarCampo(List<BloqueoDto> bloqueos, String seccion, String campo,
                                 String valor, boolean obligatorio, String mensaje) {
        if (esFaltanteONR(valor)) {
            bloqueos.add(new BloqueoDto(seccion, campo, TIPO_CAMPO_FALTANTE, mensaje, false));
        }
    }

    private boolean esFaltante(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private boolean esFaltanteONR(String valor) {
        if (esFaltante(valor)) return true;
        String v = valor.trim().toUpperCase();
        return "NR".equals(v) || "N/A".equals(v) || "NA".equals(v)
                || "-".equals(v) || "N.A.".equals(v) || "N.R.".equals(v)
                || "S/I".equals(v) || "S.I.".equals(v);
    }

    private String fmt(Object v) {
        if (v == null) return null;
        return v.toString();
    }

    private String fmtFecha(java.time.LocalDate d) {
        return d == null ? null : d.format(FMT_FECHA);
    }

    private CampoPreviewDto campo(String etiqueta, String valor, boolean obligatorio) {
        boolean faltante = esFaltanteONR(valor);
        return new CampoPreviewDto(etiqueta, faltante ? null : valor, faltante, obligatorio, false);
    }

    private List<SeccionPreviewDto> construirSecciones(Esal esal, PersoneriaJuridica pj,
                                                        Nombramiento representante,
                                                        List<Nombramiento> nombramientos,
                                                        List<OrganoAdministracion> organos,
                                                        List<ActuacionAdministrativa> actuaciones) {
        List<SeccionPreviewDto> secciones = new ArrayList<>();

        // Sección 1: Información Principal
        List<CampoPreviewDto> camposInfo = new ArrayList<>();
        camposInfo.add(campo("Nombre", esal.getNombre(), true));
        camposInfo.add(campo("ID SIPEJ", esal.getIdSipej(), true));
        camposInfo.add(campo("NIT", esal.getNit(), true));
        camposInfo.add(campo("Domicilio", esal.getDomicilio(), true));
        camposInfo.add(campo("Correo Electrónico", esal.getCorreoElectronico(), false));
        camposInfo.add(campo("Término de Duración", esal.getTerminoDuracion(), true));
        camposInfo.add(campo("Objeto Social", esal.getObjetoSocial(), true));
        secciones.add(new SeccionPreviewDto("INFORMACION PRINCIPAL", camposInfo));

        // Sección 2: Personería Jurídica
        List<CampoPreviewDto> camposPj = new ArrayList<>();
        if (pj != null) {
            camposPj.add(campo("Reconocimiento Personería Jurídica", pj.getReconocimientoPersoneriaJuridica(), true));
            camposPj.add(campo("Fecha Reconocimiento", fmtFecha(pj.getFechaReconocimientoPersoneriaJuridica()), true));
            camposPj.add(campo("Entidad que Expide", pj.getEntidadQueExpide(), true));
            camposPj.add(campo("Inscripción", pj.getInscripcion(), false));
            camposPj.add(campo("Fecha Inscripción", fmtFecha(pj.getFechaInscripcion()), false));
        }
        secciones.add(new SeccionPreviewDto("CONSTITUCION Y PERSONERIA JURIDICA", camposPj));

        // Sección 3: Representante Legal
        List<CampoPreviewDto> camposRep = new ArrayList<>();
        if (representante != null) {
            camposRep.add(campo("Nombre", representante.getNombre(), true));
            camposRep.add(campo("Tipo Documento", representante.getTipoDocumento(), false));
            camposRep.add(campo("Número Documento", representante.getNumeroDocumento(), true));
            camposRep.add(campo("Cargo", representante.getCargo(), false));
            camposRep.add(campo("Acta que Aprueba", representante.getActaAprueba(), false));
            camposRep.add(campo("Fecha Acta", fmtFecha(representante.getFechaActa()), false));
            camposRep.add(campo("Facultades y Limitaciones", representante.getFacultadesLimitaciones(), false));
        }
        secciones.add(new SeccionPreviewDto("REPRESENTANTE LEGAL", camposRep));

        // Sección 4: Órgano de Administración (primeros 3 miembros)
        List<CampoPreviewDto> camposOrgano = new ArrayList<>();
        organos.stream().limit(3).forEach(o -> {
            String prefijo = (o.getMiembro() != null ? o.getMiembro() : "Miembro") + " - ";
            camposOrgano.add(campo(prefijo + "Órgano", o.getOrgano(), false));
            camposOrgano.add(campo(prefijo + "Cargo", o.getCargo(), false));
        });
        secciones.add(new SeccionPreviewDto("ORGANO DE ADMINISTRACION", camposOrgano));

        // Sección 5: Actuaciones Administrativas (si las hay)
        if (!actuaciones.isEmpty()) {
            List<CampoPreviewDto> camposAct = new ArrayList<>();
            actuaciones.forEach(a -> {
                String tipo = a.getTipoActuacion() != null ? a.getTipoActuacion().name() : "ACTUACION";
                camposAct.add(campo(tipo + " - Acta/Resolución",
                        a.getActa() != null ? a.getActa() : a.getResolucion(), false));
                camposAct.add(campo(tipo + " - Fecha",
                        a.getFechaActa() != null ? fmtFecha(a.getFechaActa()) : fmtFecha(a.getFechaResolucion()), false));
            });
            secciones.add(new SeccionPreviewDto("ACTUACIONES ADMINISTRATIVAS", camposAct));
        }

        return secciones;
    }
}
