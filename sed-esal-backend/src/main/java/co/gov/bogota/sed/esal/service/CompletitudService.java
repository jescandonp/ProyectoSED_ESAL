package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.ActuacionAdministrativa;
import co.gov.bogota.sed.esal.domain.AdvertenciaCompletitud;
import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.OrganoAdministracion;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoActuacion;
import co.gov.bogota.sed.esal.domain.enums.TipoAdvertencia;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.CompletitudDto;
import co.gov.bogota.sed.esal.dto.CompletitudDto.AdvertenciaItemDto;
import co.gov.bogota.sed.esal.repository.ActuacionAdministrativaRepository;
import co.gov.bogota.sed.esal.repository.AdvertenciaCompletitudRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.NombramientoRepository;
import co.gov.bogota.sed.esal.repository.OrganoAdministracionRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de completitud de ESALes.
 *
 * Evalúa campos obligatorios base y reglas adicionales por estado,
 * persiste las advertencias y actualiza el semáforo en la entidad Esal.
 */
@Service
@Transactional
public class CompletitudService {

    private static final String SECCION_INFO_PRINCIPAL = "INFORMACION PRINCIPAL";
    private static final String SECCION_PERSONERIA = "CONSTITUCION Y REFORMAS";
    private static final String SECCION_NOMBRAMIENTOS = "NOMBRAMIENTOS";
    private static final String SECCION_ORGANO = "ORGANO DE ADMINISTRACION";
    private static final String SECCION_ACTUACIONES = "ACTUACIONES ADMINISTRATIVAS";

    private final EsalRepository esalRepository;
    private final PersoneriaJuridicaRepository personeriaRepository;
    private final NombramientoRepository nombramientoRepository;
    private final OrganoAdministracionRepository organoRepository;
    private final ActuacionAdministrativaRepository actuacionRepository;
    private final AdvertenciaCompletitudRepository advertenciaRepository;
    private final AuditoriaService auditoriaService;

    public CompletitudService(
            EsalRepository esalRepository,
            PersoneriaJuridicaRepository personeriaRepository,
            NombramientoRepository nombramientoRepository,
            OrganoAdministracionRepository organoRepository,
            ActuacionAdministrativaRepository actuacionRepository,
            AdvertenciaCompletitudRepository advertenciaRepository,
            AuditoriaService auditoriaService) {
        this.esalRepository = esalRepository;
        this.personeriaRepository = personeriaRepository;
        this.nombramientoRepository = nombramientoRepository;
        this.organoRepository = organoRepository;
        this.actuacionRepository = actuacionRepository;
        this.advertenciaRepository = advertenciaRepository;
        this.auditoriaService = auditoriaService;
    }

    // -------------------------------------------------------------------------
    // API pública
    // -------------------------------------------------------------------------

    /**
     * Recalcula la completitud de una ESAL:
     * 1. Evalúa campos base obligatorios (siempre aplican).
     * 2. Evalúa reglas adicionales según estado (SUSPENDIDO, EN_LIQUIDACION, CANCELADO).
     * 3. Elimina advertencias previas y persiste las nuevas.
     * 4. Actualiza estadoCompletitud en Esal.
     *
     * @param esalId ID de la ESAL
     * @return DTO con el resultado de completitud
     */
    public CompletitudDto calcular(Long esalId) {
        Esal esal = obtenerEsal(esalId);

        // Eliminar advertencias previas
        List<AdvertenciaCompletitud> previas = advertenciaRepository.findByEsalId(esalId);
        advertenciaRepository.deleteAll(previas);

        // Evaluar reglas
        List<AdvertenciaCompletitud> nuevas = evaluarReglas(esal);

        // Persistir nuevas advertencias
        LocalDateTime ahora = LocalDateTime.now();
        for (AdvertenciaCompletitud adv : nuevas) {
            adv.setCreatedAt(ahora);
            advertenciaRepository.save(adv);
        }

        // Calcular semáforo
        EstadoCompletitud semaforo = calcularSemaforo(nuevas);
        esal.setEstadoCompletitud(semaforo);
        esal.setUpdatedAt(ahora);
        esalRepository.save(esal);

        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String usuario = (auth != null) ? auth.getName() : "sistema";
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.RECALCULAR_COMPLETITUD,
                AuditoriaAcciones.ENTIDAD_ESAL,
                esalId, esal.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO,
                "Semaforo: " + semaforo.name() + ", Advertencias: " + nuevas.size());

        return construirDto(esal, nuevas);
    }

    /**
     * Consulta el estado de completitud sin recalcular.
     * Lee las advertencias existentes en base de datos.
     *
     * @param esalId ID de la ESAL
     * @return DTO con el resultado de completitud actual
     */
    @Transactional(readOnly = true)
    public CompletitudDto consultar(Long esalId) {
        Esal esal = obtenerEsal(esalId);
        List<AdvertenciaCompletitud> advertencias = advertenciaRepository.findByEsalId(esalId);

        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String usuario = (auth != null) ? auth.getName() : "sistema";
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.CONSULTAR_COMPLETITUD,
                AuditoriaAcciones.ENTIDAD_ESAL,
                esalId, esal.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO, null);

        return construirDto(esal, advertencias);
    }

    // -------------------------------------------------------------------------
    // Lógica de evaluación
    // -------------------------------------------------------------------------

    private List<AdvertenciaCompletitud> evaluarReglas(Esal esal) {
        List<AdvertenciaCompletitud> advertencias = new ArrayList<>();
        Long esalId = esal.getId();

        // --- Campos base obligatorios (bloqueantes para todos los estados) ---
        evaluarCampoBase(advertencias, esalId,
                SECCION_INFO_PRINCIPAL, "NOMBRE",
                esal.getNombre() == null || esal.getNombre().trim().isEmpty(),
                "El nombre de la ESAL es obligatorio y está faltante.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_INFO_PRINCIPAL, "ID SIPEJ",
                esFaltanteONR(esal.getIdSipej()),
                "El ID SIPEJ es obligatorio y está faltante o tiene valor NR.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_INFO_PRINCIPAL, "DOMICILIO",
                esFaltante(esal.getDomicilio()),
                "El domicilio es obligatorio y está faltante.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_INFO_PRINCIPAL, "CORREO ELECTRONICO",
                esFaltante(esal.getCorreoElectronico()),
                "El correo electrónico es obligatorio y está faltante.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_INFO_PRINCIPAL, "TERMINO DE DURACION",
                esFaltante(esal.getTerminoDuracion()),
                "El término de duración es obligatorio y está faltante.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_INFO_PRINCIPAL, "OBJETO SOCIAL",
                esFaltante(esal.getObjetoSocial()),
                "El objeto social es obligatorio y está faltante.");

        // Personería jurídica
        List<PersoneriaJuridica> pjs = personeriaRepository.findByEsalId(esalId);
        PersoneriaJuridica pj = pjs.isEmpty() ? null : pjs.get(0);

        evaluarCampoBase(advertencias, esalId,
                SECCION_PERSONERIA, "RECONOCIMIENTO DE PERSONERIA JURIDICA",
                pj == null || esFaltante(pj.getReconocimientoPersoneriaJuridica()),
                "El reconocimiento de personería jurídica es obligatorio y está faltante.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_PERSONERIA, "FECHA RECONOCIMIENTO PERSONERIA JURIDICA",
                pj == null || pj.getFechaReconocimientoPersoneriaJuridica() == null,
                "La fecha de reconocimiento de personería jurídica es obligatoria y está faltante.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_PERSONERIA, "ENTIDAD QUE EXPIDE",
                pj == null || esFaltante(pj.getEntidadQueExpide()),
                "La entidad que expide la personería jurídica es obligatoria y está faltante.");

        // Representante legal
        List<Nombramiento> rls = nombramientoRepository.findByEsalIdAndTipoNombramiento(
                esalId, TipoNombramiento.REPRESENTANTE_LEGAL);
        Nombramiento rl = rls.isEmpty() ? null : rls.get(0);

        evaluarCampoBase(advertencias, esalId,
                SECCION_NOMBRAMIENTOS, "REPRESENTANTE LEGAL",
                rl == null || esFaltante(rl.getNombre()),
                "El nombre del representante legal es obligatorio y está faltante.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_NOMBRAMIENTOS, "NUMERO DE DOCUMENTO RL",
                rl == null || esFaltante(rl.getNumeroDocumento()),
                "El número de documento del representante legal es obligatorio y está faltante.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_NOMBRAMIENTOS, "ACTA APRUEBA RL",
                rl == null || esFaltante(rl.getActaAprueba()),
                "El acta que aprueba el representante legal es obligatoria y está faltante.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_NOMBRAMIENTOS, "FECHA ACTA RL",
                rl == null || rl.getFechaActa() == null,
                "La fecha del acta del representante legal es obligatoria y está faltante.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_NOMBRAMIENTOS, "FACULTADES Y LIMITACIONES RL",
                rl == null || esFaltante(rl.getFacultadesLimitaciones()),
                "Las facultades/limitaciones del representante legal son obligatorias y están faltantes.");

        // Órgano de administración
        List<OrganoAdministracion> organos = organoRepository.findByEsalId(esalId);
        boolean tieneOrgano = organos.stream().anyMatch(
                o -> !esFaltante(o.getOrgano()) || !esFaltante(o.getMiembro()));
        evaluarCampoBase(advertencias, esalId,
                SECCION_ORGANO, "ORGANO DE ADMINISTRACION",
                !tieneOrgano,
                "Se requiere al menos un órgano de administración con organo o miembro informado.");

        // --- Reglas adicionales por estado ---
        EstadoEsal estado = esal.getEstado();

        if (EstadoEsal.SUSPENDIDO.equals(estado)) {
            evaluarReglasEstadoSuspendido(advertencias, esalId);
        } else if (EstadoEsal.EN_LIQUIDACION.equals(estado)) {
            evaluarReglasEstadoLiquidacion(advertencias, esalId);
        } else if (EstadoEsal.CANCELADO.equals(estado)) {
            evaluarReglasEstadoCancelado(advertencias, esalId);
        }

        return advertencias;
    }

    /**
     * Reglas adicionales para estado SUSPENDIDO.
     * Exige al menos una ActuacionAdministrativa de tipo SUSPENSION
     * con tiempoSuspension y fechaInicio informados.
     */
    private void evaluarReglasEstadoSuspendido(List<AdvertenciaCompletitud> advertencias, Long esalId) {
        List<ActuacionAdministrativa> suspensiones = actuacionRepository.findByEsalId(esalId)
                .stream()
                .filter(a -> TipoActuacion.SUSPENSION.equals(a.getTipoActuacion()))
                .collect(Collectors.toList());

        if (suspensiones.isEmpty()) {
            advertencias.add(crearAdvertencia(esalId,
                    SECCION_ACTUACIONES, "ACTUACION SUSPENSION",
                    "El estado SUSPENDIDO requiere al menos una actuación administrativa de tipo SUSPENSION.",
                    true));
            return;
        }

        ActuacionAdministrativa suspension = suspensiones.get(0);

        evaluarCampoBase(advertencias, esalId,
                SECCION_ACTUACIONES, "TIEMPO SUSPENSION",
                esFaltante(suspension.getTiempoSuspension()),
                "El tiempo de suspensión es obligatorio para el estado SUSPENDIDO.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_ACTUACIONES, "FECHA INICIO SUSPENSION",
                suspension.getFechaInicio() == null,
                "La fecha de inicio de suspensión es obligatoria para el estado SUSPENDIDO.");
    }

    /**
     * Reglas adicionales para estado EN_LIQUIDACION.
     * Exige al menos una ActuacionAdministrativa de tipo LIQUIDACION
     * con acta y fechaActa informados.
     */
    private void evaluarReglasEstadoLiquidacion(List<AdvertenciaCompletitud> advertencias, Long esalId) {
        List<ActuacionAdministrativa> liquidaciones = actuacionRepository.findByEsalId(esalId)
                .stream()
                .filter(a -> TipoActuacion.LIQUIDACION.equals(a.getTipoActuacion()))
                .collect(Collectors.toList());

        if (liquidaciones.isEmpty()) {
            advertencias.add(crearAdvertencia(esalId,
                    SECCION_ACTUACIONES, "ACTUACION LIQUIDACION",
                    "El estado EN_LIQUIDACION requiere al menos una actuación administrativa de tipo LIQUIDACION.",
                    true));
            return;
        }

        ActuacionAdministrativa liquidacion = liquidaciones.get(0);

        evaluarCampoBase(advertencias, esalId,
                SECCION_ACTUACIONES, "ACTA LIQUIDACION",
                esFaltante(liquidacion.getActa()),
                "El acta de liquidación es obligatoria para el estado EN_LIQUIDACION.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_ACTUACIONES, "FECHA ACTA LIQUIDACION",
                liquidacion.getFechaActa() == null,
                "La fecha del acta de liquidación es obligatoria para el estado EN_LIQUIDACION.");
    }

    /**
     * Reglas adicionales para estado CANCELADO.
     * Exige al menos una ActuacionAdministrativa de tipo CANCELACION
     * con resolucion y fechaResolucion informados.
     */
    private void evaluarReglasEstadoCancelado(List<AdvertenciaCompletitud> advertencias, Long esalId) {
        List<ActuacionAdministrativa> cancelaciones = actuacionRepository.findByEsalId(esalId)
                .stream()
                .filter(a -> TipoActuacion.CANCELACION.equals(a.getTipoActuacion()))
                .collect(Collectors.toList());

        if (cancelaciones.isEmpty()) {
            advertencias.add(crearAdvertencia(esalId,
                    SECCION_ACTUACIONES, "ACTUACION CANCELACION",
                    "El estado CANCELADO requiere al menos una actuación administrativa de tipo CANCELACION.",
                    true));
            return;
        }

        ActuacionAdministrativa cancelacion = cancelaciones.get(0);

        evaluarCampoBase(advertencias, esalId,
                SECCION_ACTUACIONES, "RESOLUCION CANCELACION",
                esFaltante(cancelacion.getResolucion()),
                "La resolución de cancelación es obligatoria para el estado CANCELADO.");

        evaluarCampoBase(advertencias, esalId,
                SECCION_ACTUACIONES, "FECHA RESOLUCION CANCELACION",
                cancelacion.getFechaResolucion() == null,
                "La fecha de resolución de cancelación es obligatoria para el estado CANCELADO.");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void evaluarCampoBase(List<AdvertenciaCompletitud> advertencias,
                                   Long esalId, String seccion, String campo,
                                   boolean condicionFallo, String mensaje) {
        if (condicionFallo) {
            advertencias.add(crearAdvertencia(esalId, seccion, campo, mensaje, true));
        }
    }

    private AdvertenciaCompletitud crearAdvertencia(Long esalId, String seccion,
                                                     String campo, String mensaje,
                                                     boolean bloqueante) {
        AdvertenciaCompletitud adv = new AdvertenciaCompletitud();
        adv.setEsalId(esalId);
        adv.setSeccion(seccion);
        adv.setCampo(campo);
        adv.setTipo(TipoAdvertencia.CAMPO_OBLIGATORIO_FALTANTE);
        adv.setBloqueante(bloqueante);
        adv.setMensaje(mensaje);
        return adv;
    }

    private EstadoCompletitud calcularSemaforo(List<AdvertenciaCompletitud> advertencias) {
        boolean tieneBloqueante = advertencias.stream()
                .anyMatch(a -> Boolean.TRUE.equals(a.getBloqueante()));
        if (tieneBloqueante) {
            return EstadoCompletitud.INCOMPLETO_BLOQUEANTE;
        }
        boolean tieneNoBloqueante = advertencias.stream()
                .anyMatch(a -> Boolean.FALSE.equals(a.getBloqueante()));
        if (tieneNoBloqueante) {
            return EstadoCompletitud.INCOMPLETO_NO_BLOQUEANTE;
        }
        return EstadoCompletitud.LISTO_PARA_CERTIFICAR;
    }

    private CompletitudDto construirDto(Esal esal, List<AdvertenciaCompletitud> advertencias) {
        CompletitudDto dto = new CompletitudDto();
        dto.setEsalId(esal.getId());
        dto.setIdSipej(esal.getIdSipej());
        dto.setNombre(esal.getNombre());
        dto.setEstado(esal.getEstado());
        dto.setEstadoCompletitud(esal.getEstadoCompletitud());

        long bloqueantes = advertencias.stream()
                .filter(a -> Boolean.TRUE.equals(a.getBloqueante())).count();
        long noBloqueantes = advertencias.stream()
                .filter(a -> Boolean.FALSE.equals(a.getBloqueante())).count();

        dto.setTotalAdvertencias(advertencias.size());
        dto.setAdvertenciasBloqueantes((int) bloqueantes);
        dto.setAdvertenciasNoBloqueantes((int) noBloqueantes);

        List<AdvertenciaItemDto> items = advertencias.stream().map(a -> {
            AdvertenciaItemDto item = new AdvertenciaItemDto();
            item.setSeccion(a.getSeccion());
            item.setCampo(a.getCampo());
            item.setTipo(a.getTipo() != null ? a.getTipo().name() : null);
            item.setBloqueante(Boolean.TRUE.equals(a.getBloqueante()));
            item.setMensaje(a.getMensaje());
            return item;
        }).collect(Collectors.toList());

        dto.setAdvertencias(items);
        return dto;
    }

    private Esal obtenerEsal(Long esalId) {
        return esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));
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
}
