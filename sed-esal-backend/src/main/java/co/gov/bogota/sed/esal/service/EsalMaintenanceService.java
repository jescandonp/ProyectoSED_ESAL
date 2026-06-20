package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.OrganoAdministracion;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.ActuacionAdministrativa;
import co.gov.bogota.sed.esal.domain.AdvertenciaCompletitud;
import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.domain.enums.TipoActuacion;
import co.gov.bogota.sed.esal.domain.enums.TipoDocumentoSoporte;
import co.gov.bogota.sed.esal.domain.enums.TipoAdvertencia;
import co.gov.bogota.sed.esal.domain.enums.TipoNombramiento;
import co.gov.bogota.sed.esal.dto.CancelacionEsalDto;
import co.gov.bogota.sed.esal.dto.EsalInformacionPrincipalDto;
import co.gov.bogota.sed.esal.dto.MantenimientoEsalDto;
import co.gov.bogota.sed.esal.dto.NombramientoDto;
import co.gov.bogota.sed.esal.dto.OrganoAdministracionDto;
import co.gov.bogota.sed.esal.dto.PersoneriaJuridicaDto;
import co.gov.bogota.sed.esal.dto.ReactivacionEsalDto;
import co.gov.bogota.sed.esal.repository.ActuacionAdministrativaRepository;
import co.gov.bogota.sed.esal.repository.AdvertenciaCompletitudRepository;
import co.gov.bogota.sed.esal.repository.DocumentoSoporteRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.NombramientoRepository;
import co.gov.bogota.sed.esal.repository.OrganoAdministracionRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EsalMaintenanceService {

    private final EsalRepository esalRepository;
    private final PersoneriaJuridicaRepository personeriaRepository;
    private final NombramientoRepository nombramientoRepository;
    private final OrganoAdministracionRepository organoRepository;
    private final ActuacionAdministrativaRepository actuacionRepository;
    private final DocumentoSoporteRepository documentoRepository;
    private final AdvertenciaCompletitudRepository advertenciaRepository;
    private final CompletitudService completitudService;
    private final AuditoriaService auditoriaService;

    public EsalMaintenanceService(EsalRepository esalRepository,
                                  PersoneriaJuridicaRepository personeriaRepository,
                                  NombramientoRepository nombramientoRepository,
                                  OrganoAdministracionRepository organoRepository,
                                  ActuacionAdministrativaRepository actuacionRepository,
                                  DocumentoSoporteRepository documentoRepository,
                                  AdvertenciaCompletitudRepository advertenciaRepository,
                                  CompletitudService completitudService,
                                  AuditoriaService auditoriaService) {
        this.esalRepository = esalRepository;
        this.personeriaRepository = personeriaRepository;
        this.nombramientoRepository = nombramientoRepository;
        this.organoRepository = organoRepository;
        this.actuacionRepository = actuacionRepository;
        this.documentoRepository = documentoRepository;
        this.advertenciaRepository = advertenciaRepository;
        this.completitudService = completitudService;
        this.auditoriaService = auditoriaService;
    }

    public MantenimientoEsalDto crear(EsalInformacionPrincipalDto dto, String usuario) {
        validarNombre(dto);
        validarIdSipejUnico(dto.getIdSipej(), null);

        Esal esal = new Esal();
        aplicarInformacionPrincipal(esal, dto, true);
        esal.setCreatedAt(LocalDateTime.now());
        esal.setCreatedBy(usuario);
        Esal saved = esalRepository.save(esal);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.ESAL_CREADA,
                AuditoriaAcciones.ENTIDAD_ESAL,
                saved.getId(), saved.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO, null);

        completitudService.calcular(saved.getId());
        return obtenerMantenimiento(saved.getId());
    }

    @Transactional(readOnly = true)
    public MantenimientoEsalDto obtenerMantenimiento(Long esalId) {
        Esal esal = obtenerEsal(esalId);
        MantenimientoEsalDto dto = new MantenimientoEsalDto();
        dto.setId(esal.getId());
        dto.setInformacionPrincipal(toInformacionPrincipalDto(esal));
        dto.setPersoneriaJuridica(obtenerPersoneria(esal.getId()));
        dto.setRepresentantes(listarRepresentantes(esal.getId()));
        dto.setOrganosAdministracion(listarMiembrosOrgano(esal.getId()));
        return dto;
    }

    public MantenimientoEsalDto actualizarInformacionPrincipal(Long esalId,
                                                               EsalInformacionPrincipalDto dto,
                                                               String usuario) {
        Esal esal = obtenerEsal(esalId);
        validarIdSipejUnico(dto.getIdSipej(), esal.getId());
        if (EstadoEsal.EN_LIQUIDACION.equals(dto.getEstado())
                && !existeDocumentoVigente(esalId, TipoDocumentoSoporte.LIQUIDACION)) {
            registrarBloqueoDocumentoObligatorio(usuario, esal,
                    AuditoriaAcciones.ESAL_LIQUIDACION_BLOQUEADA_SIN_DOCUMENTO,
                    "No se puede pasar a EN_LIQUIDACION sin documento vigente de liquidacion.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede pasar a EN_LIQUIDACION sin documento vigente de liquidacion.");
        }
        aplicarInformacionPrincipal(esal, dto, false);
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setUpdatedBy(usuario);
        Esal saved = esalRepository.save(esal);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.ESAL_INFORMACION_PRINCIPAL_ACTUALIZADA,
                AuditoriaAcciones.ENTIDAD_ESAL,
                saved.getId(), saved.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO, null);

        completitudService.calcular(saved.getId());
        return obtenerMantenimiento(saved.getId());
    }

    public MantenimientoEsalDto guardarPersoneriaJuridica(Long esalId,
                                                          PersoneriaJuridicaDto dto,
                                                          String usuario) {
        Esal esal = obtenerEsal(esalId);
        PersoneriaJuridica personeria = buscarPersoneriaEntidad(esalId);
        if (personeria == null) {
            personeria = new PersoneriaJuridica();
            personeria.setEsalId(esalId);
        }
        personeria.setReconocimientoPersoneriaJuridica(dto.getReconocimientoPersoneriaJuridica());
        personeria.setFechaReconocimientoPersoneriaJuridica(dto.getFechaReconocimientoPersoneriaJuridica());
        personeria.setEntidadQueExpide(dto.getEntidadQueExpide());
        personeria.setInscripcion(dto.getInscripcion());
        personeria.setFechaInscripcion(dto.getFechaInscripcion());
        personeria.setEntidadQueInscribio(dto.getEntidadQueInscribio());
        personeriaRepository.save(personeria);

        esal.setUpdatedAt(LocalDateTime.now());
        esal.setUpdatedBy(usuario);
        esalRepository.save(esal);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.ESAL_PERSONERIA_ACTUALIZADA,
                AuditoriaAcciones.ENTIDAD_ESAL,
                esal.getId(), esal.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO, null);

        completitudService.calcular(esalId);
        return obtenerMantenimiento(esalId);
    }

    @Transactional(readOnly = true)
    public List<NombramientoDto> listarRepresentantes(Long esalId) {
        obtenerEsal(esalId);
        return nombramientoRepository.findByEsalId(esalId).stream()
                .filter(n -> esTipoRepresentantePermitido(n.getTipoNombramiento()))
                .map(this::toNombramientoDto)
                .collect(Collectors.toList());
    }

    public NombramientoDto crearRepresentante(Long esalId, NombramientoDto dto, String usuario) {
        Esal esal = obtenerEsal(esalId);
        validarTipoRepresentante(dto.getTipoNombramiento());

        Nombramiento nombramiento = new Nombramiento();
        nombramiento.setEsalId(esalId);
        aplicarNombramiento(nombramiento, dto);
        Nombramiento saved = nombramientoRepository.save(nombramiento);

        registrarAuditoriaNombramiento(usuario, AuditoriaAcciones.ESAL_REPRESENTANTE_CREADO, esal, saved);
        tocarEsalYRecalcular(esal, usuario);

        return toNombramientoDto(saved);
    }

    public NombramientoDto actualizarRepresentante(Long esalId,
                                                   Long representanteId,
                                                   NombramientoDto dto,
                                                   String usuario) {
        Esal esal = obtenerEsal(esalId);
        Nombramiento nombramiento = obtenerNombramiento(esalId, representanteId);
        if (dto.getTipoNombramiento() != null) {
            validarTipoRepresentante(dto.getTipoNombramiento());
        }
        Boolean vigenteAntes = nombramiento.getVigente();
        aplicarNombramiento(nombramiento, dto);
        Nombramiento saved = nombramientoRepository.save(nombramiento);

        String accion = vigenteAntes != null && dto.getVigente() != null && !vigenteAntes.equals(dto.getVigente())
                ? AuditoriaAcciones.ESAL_REPRESENTANTE_VIGENCIA_CAMBIADA
                : AuditoriaAcciones.ESAL_REPRESENTANTE_ACTUALIZADO;
        registrarAuditoriaNombramiento(usuario, accion, esal, saved);
        tocarEsalYRecalcular(esal, usuario);

        return toNombramientoDto(saved);
    }

    @Transactional(readOnly = true)
    public List<OrganoAdministracionDto> listarMiembrosOrgano(Long esalId) {
        obtenerEsal(esalId);
        return organoRepository.findByEsalId(esalId).stream()
                .map(this::toOrganoAdministracionDto)
                .collect(Collectors.toList());
    }

    public OrganoAdministracionDto crearMiembroOrgano(Long esalId, OrganoAdministracionDto dto, String usuario) {
        Esal esal = obtenerEsal(esalId);

        OrganoAdministracion organo = new OrganoAdministracion();
        organo.setEsalId(esalId);
        aplicarOrganoAdministracion(organo, dto);
        OrganoAdministracion saved = organoRepository.save(organo);

        registrarAuditoriaOrgano(usuario, AuditoriaAcciones.ESAL_ORGANO_MIEMBRO_CREADO, esal, saved);
        tocarEsalYRecalcular(esal, usuario);

        return toOrganoAdministracionDto(saved);
    }

    public OrganoAdministracionDto actualizarMiembroOrgano(Long esalId,
                                                           Long miembroId,
                                                           OrganoAdministracionDto dto,
                                                           String usuario) {
        Esal esal = obtenerEsal(esalId);
        OrganoAdministracion organo = obtenerMiembroOrgano(esalId, miembroId);

        aplicarOrganoAdministracion(organo, dto);
        OrganoAdministracion saved = organoRepository.save(organo);

        registrarAuditoriaOrgano(usuario, AuditoriaAcciones.ESAL_ORGANO_MIEMBRO_ACTUALIZADO, esal, saved);
        tocarEsalYRecalcular(esal, usuario);

        return toOrganoAdministracionDto(saved);
    }

    public MantenimientoEsalDto cancelar(Long esalId, CancelacionEsalDto dto, String usuario) {
        validarCancelacion(dto);
        Esal esal = obtenerEsal(esalId);
        if (!existeDocumentoVigente(esalId, TipoDocumentoSoporte.CANCELACION)) {
            registrarBloqueoDocumentoObligatorio(usuario, esal,
                    AuditoriaAcciones.ESAL_CANCELACION_BLOQUEADA_SIN_DOCUMENTO,
                    "No se puede cancelar la ESAL sin documento vigente de cancelacion.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede cancelar la ESAL sin documento vigente de cancelacion.");
        }

        ActuacionAdministrativa actuacion = new ActuacionAdministrativa();
        actuacion.setEsalId(esalId);
        actuacion.setTipoActuacion(TipoActuacion.CANCELACION);
        actuacion.setResolucion(dto.getResolucion().trim());
        actuacion.setFechaResolucion(dto.getFechaResolucion());
        actuacion.setMotivo(dto.getMotivo().trim());
        ActuacionAdministrativa saved = actuacionRepository.save(actuacion);

        esal.setEstado(EstadoEsal.CANCELADO);
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setUpdatedBy(usuario);
        esalRepository.save(esal);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.ESAL_CANCELADA,
                AuditoriaAcciones.ENTIDAD_ESAL,
                esal.getId(), esal.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO,
                "Actuacion cancelacion: " + saved.getId());

        completitudService.calcular(esalId);

        return obtenerMantenimiento(esalId);
    }

    public MantenimientoEsalDto reactivar(Long esalId, ReactivacionEsalDto dto, String usuario) {
        validarReactivacion(dto);
        Esal esal = obtenerEsal(esalId);
        if (!EstadoEsal.CANCELADO.equals(esal.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La reactivacion solo aplica para ESAL en estado CANCELADO.");
        }

        EstadoEsal estadoDestino = dto.getEstadoDestino() == null ? EstadoEsal.ACTIVO : dto.getEstadoDestino();
        esal.setEstado(estadoDestino);
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setUpdatedBy(usuario);
        esalRepository.save(esal);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.ESAL_REACTIVADA,
                AuditoriaAcciones.ENTIDAD_ESAL,
                esal.getId(), esal.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO,
                "Estado destino: " + estadoDestino + ". Motivo: " + dto.getMotivo().trim());

        completitudService.calcular(esalId);
        return obtenerMantenimiento(esalId);
    }

    private void aplicarInformacionPrincipal(Esal esal, EsalInformacionPrincipalDto dto, boolean crear) {
        if (dto.getNombre() != null) {
            esal.setNombre(dto.getNombre().trim());
        }
        if (dto.getIdSipej() != null) {
            esal.setIdSipej(dto.getIdSipej());
        }
        if (dto.getNit() != null) {
            esal.setNit(dto.getNit());
        }
        if (dto.getDomicilio() != null) {
            esal.setDomicilio(dto.getDomicilio());
        }
        if (dto.getCorreoElectronico() != null) {
            esal.setCorreoElectronico(dto.getCorreoElectronico());
        }
        if (dto.getTerminoDuracion() != null) {
            esal.setTerminoDuracion(dto.getTerminoDuracion());
        }
        if (dto.getObjetoSocial() != null) {
            esal.setObjetoSocial(dto.getObjetoSocial());
        }
        if (crear && dto.getEstado() != null) {
            esal.setEstado(dto.getEstado());
        } else if (!crear && dto.getEstado() != null && !EstadoEsal.CANCELADO.equals(dto.getEstado())) {
            esal.setEstado(dto.getEstado());
        }
    }

    private void validarNombre(EsalInformacionPrincipalDto dto) {
        if (dto == null || dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'nombre' es obligatorio.");
        }
    }

    private void validarIdSipejUnico(String idSipej, Long esalActualId) {
        if (idSipej == null || idSipej.trim().isEmpty()) {
            return;
        }
        esalRepository.findByIdSipej(idSipej).ifPresent(existente -> {
            if (esalActualId == null || !existente.getId().equals(esalActualId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID SIPEJ ya existe.");
            }
        });
    }

    private Esal obtenerEsal(Long esalId) {
        return esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));
    }

    private PersoneriaJuridica buscarPersoneriaEntidad(Long esalId) {
        List<PersoneriaJuridica> registros = personeriaRepository.findByEsalId(esalId);
        return registros.isEmpty() ? null : registros.get(0);
    }

    private Nombramiento obtenerNombramiento(Long esalId, Long representanteId) {
        Nombramiento nombramiento = nombramientoRepository.findById(representanteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Representante no encontrado con id: " + representanteId));
        if (!esalId.equals(nombramiento.getEsalId()) || !esTipoRepresentantePermitido(nombramiento.getTipoNombramiento())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Representante no encontrado con id: " + representanteId);
        }
        return nombramiento;
    }

    private OrganoAdministracion obtenerMiembroOrgano(Long esalId, Long miembroId) {
        OrganoAdministracion organo = organoRepository.findById(miembroId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Miembro de organo no encontrado con id: " + miembroId));
        if (!esalId.equals(organo.getEsalId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Miembro de organo no encontrado con id: " + miembroId);
        }
        return organo;
    }

    private void validarTipoRepresentante(TipoNombramiento tipo) {
        if (!esTipoRepresentantePermitido(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El campo 'tipoNombramiento' solo permite REPRESENTANTE_LEGAL o REPRESENTANTE_LEGAL_SUPLENTE.");
        }
    }

    private void validarCancelacion(CancelacionEsalDto dto) {
        if (dto == null || dto.getResolucion() == null || dto.getResolucion().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'resolucion' es obligatorio.");
        }
        if (dto.getFechaResolucion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'fechaResolucion' es obligatorio.");
        }
        if (dto.getMotivo() == null || dto.getMotivo().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'motivo' es obligatorio.");
        }
    }

    private void validarReactivacion(ReactivacionEsalDto dto) {
        if (dto == null || dto.getMotivo() == null || dto.getMotivo().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'motivo' es obligatorio.");
        }
        if (EstadoEsal.CANCELADO.equals(dto.getEstadoDestino())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El campo 'estadoDestino' no puede ser CANCELADO en una reactivacion.");
        }
    }

    private boolean esTipoRepresentantePermitido(TipoNombramiento tipo) {
        return TipoNombramiento.REPRESENTANTE_LEGAL.equals(tipo)
                || TipoNombramiento.REPRESENTANTE_LEGAL_SUPLENTE.equals(tipo);
    }

    private void aplicarNombramiento(Nombramiento nombramiento, NombramientoDto dto) {
        if (dto.getTipoNombramiento() != null) {
            nombramiento.setTipoNombramiento(dto.getTipoNombramiento());
        }
        if (dto.getNombre() != null) {
            nombramiento.setNombre(dto.getNombre());
        }
        if (dto.getTipoDocumento() != null) {
            nombramiento.setTipoDocumento(dto.getTipoDocumento());
        }
        if (dto.getNumeroDocumento() != null) {
            nombramiento.setNumeroDocumento(dto.getNumeroDocumento());
        }
        if (dto.getCargo() != null) {
            nombramiento.setCargo(dto.getCargo());
        }
        if (dto.getActaAprueba() != null) {
            nombramiento.setActaAprueba(dto.getActaAprueba());
        }
        if (dto.getFechaActa() != null) {
            nombramiento.setFechaActa(dto.getFechaActa());
        }
        if (dto.getTarjetaProfesional() != null) {
            nombramiento.setTarjetaProfesional(dto.getTarjetaProfesional());
        }
        if (dto.getFacultadesLimitaciones() != null) {
            nombramiento.setFacultadesLimitaciones(dto.getFacultadesLimitaciones());
        }
        if (dto.getVigente() != null) {
            nombramiento.setVigente(dto.getVigente());
        }
    }

    private void aplicarOrganoAdministracion(OrganoAdministracion organo, OrganoAdministracionDto dto) {
        if (dto.getOrgano() != null) {
            organo.setOrgano(dto.getOrgano());
        }
        if (dto.getMiembro() != null) {
            organo.setMiembro(dto.getMiembro());
        }
        if (dto.getCargo() != null) {
            organo.setCargo(dto.getCargo());
        }
        if (dto.getTipoDocumento() != null) {
            organo.setTipoDocumento(dto.getTipoDocumento());
        }
        if (dto.getNumeroDocumento() != null) {
            organo.setNumeroDocumento(dto.getNumeroDocumento());
        }
        if (dto.getActaAprueba() != null) {
            organo.setActaAprueba(dto.getActaAprueba());
        }
        if (dto.getFechaActa() != null) {
            organo.setFechaActa(dto.getFechaActa());
        }
        if (dto.getActaAclaratoria() != null) {
            organo.setActaAclaratoria(dto.getActaAclaratoria());
        }
        if (dto.getFechaActaAclaratoria() != null) {
            organo.setFechaActaAclaratoria(dto.getFechaActaAclaratoria());
        }
        if (dto.getFacultadesLimitaciones() != null) {
            organo.setFacultadesLimitaciones(dto.getFacultadesLimitaciones());
        }
    }

    private void tocarEsalYRecalcular(Esal esal, String usuario) {
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setUpdatedBy(usuario);
        esalRepository.save(esal);
        completitudService.calcular(esal.getId());
    }

    private void registrarAuditoriaNombramiento(String usuario, String accion, Esal esal, Nombramiento nombramiento) {
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                accion,
                AuditoriaAcciones.ENTIDAD_ESAL,
                esal.getId(), esal.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO,
                "Nombramiento: " + nombramiento.getId());
    }

    private void registrarAuditoriaOrgano(String usuario, String accion, Esal esal, OrganoAdministracion organo) {
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                accion,
                AuditoriaAcciones.ENTIDAD_ESAL,
                esal.getId(), esal.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO,
                "OrganoAdministracion: " + organo.getId());
    }

    private boolean existeDocumentoVigente(Long esalId, TipoDocumentoSoporte tipoDocumental) {
        return documentoRepository.findByEsalId(esalId).stream()
                .anyMatch(documento -> tipoDocumental.equals(documento.getTipoDocumental())
                        && Boolean.TRUE.equals(documento.getVigente()));
    }

    private void registrarBloqueoDocumentoObligatorio(String usuario, Esal esal, String accion, String detalle) {
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                accion,
                AuditoriaAcciones.ENTIDAD_ESAL,
                esal.getId(), esal.getIdSipej(),
                AuditoriaAcciones.RESULTADO_ERROR,
                detalle);
    }

    private void registrarAdvertenciaSoporteCancelacionSiFalta(Long esalId) {
        boolean existePdfCancelacion = documentoRepository.findByEsalId(esalId).stream()
                .anyMatch(this::esSoportePdfCancelacion);
        if (existePdfCancelacion) {
            return;
        }

        AdvertenciaCompletitud advertencia = new AdvertenciaCompletitud();
        advertencia.setEsalId(esalId);
        advertencia.setSeccion("ACTUACIONES");
        advertencia.setCampo("PDF SOPORTE CANCELACION");
        advertencia.setTipo(TipoAdvertencia.DOCUMENTO_REQUERIDO_FALTANTE);
        advertencia.setBloqueante(Boolean.FALSE);
        advertencia.setMensaje("La cancelacion fue registrada sin PDF soporte asociado.");
        advertencia.setCreatedAt(LocalDateTime.now());
        advertenciaRepository.save(advertencia);
    }

    private boolean esSoportePdfCancelacion(DocumentoSoporte documento) {
        if (!"application/pdf".equalsIgnoreCase(documento.getContentType())) {
            return false;
        }
        String tipoProceso = documento.getTipoProceso() == null ? "" : documento.getTipoProceso().toUpperCase();
        String tipoDocumento = documento.getTipoDocumento() == null ? "" : documento.getTipoDocumento().toUpperCase();
        return tipoProceso.contains("CANCELACION") || tipoDocumento.contains("CANCELACION");
    }

    private PersoneriaJuridicaDto obtenerPersoneria(Long esalId) {
        PersoneriaJuridica personeria = buscarPersoneriaEntidad(esalId);
        return personeria == null ? null : toPersoneriaDto(personeria);
    }

    private EsalInformacionPrincipalDto toInformacionPrincipalDto(Esal esal) {
        EsalInformacionPrincipalDto dto = new EsalInformacionPrincipalDto();
        dto.setId(esal.getId());
        dto.setNombre(esal.getNombre());
        dto.setIdSipej(esal.getIdSipej());
        dto.setNit(esal.getNit());
        dto.setDomicilio(esal.getDomicilio());
        dto.setCorreoElectronico(esal.getCorreoElectronico());
        dto.setTerminoDuracion(esal.getTerminoDuracion());
        dto.setObjetoSocial(esal.getObjetoSocial());
        dto.setEstado(esal.getEstado());
        dto.setEstadoCompletitud(esal.getEstadoCompletitud());
        return dto;
    }

    private PersoneriaJuridicaDto toPersoneriaDto(PersoneriaJuridica personeria) {
        PersoneriaJuridicaDto dto = new PersoneriaJuridicaDto();
        dto.setId(personeria.getId());
        dto.setEsalId(personeria.getEsalId());
        dto.setReconocimientoPersoneriaJuridica(personeria.getReconocimientoPersoneriaJuridica());
        dto.setFechaReconocimientoPersoneriaJuridica(personeria.getFechaReconocimientoPersoneriaJuridica());
        dto.setEntidadQueExpide(personeria.getEntidadQueExpide());
        dto.setInscripcion(personeria.getInscripcion());
        dto.setFechaInscripcion(personeria.getFechaInscripcion());
        dto.setEntidadQueInscribio(personeria.getEntidadQueInscribio());
        return dto;
    }

    private NombramientoDto toNombramientoDto(Nombramiento nombramiento) {
        NombramientoDto dto = new NombramientoDto();
        dto.setId(nombramiento.getId());
        dto.setEsalId(nombramiento.getEsalId());
        dto.setTipoNombramiento(nombramiento.getTipoNombramiento());
        dto.setNombre(nombramiento.getNombre());
        dto.setTipoDocumento(nombramiento.getTipoDocumento());
        dto.setNumeroDocumento(nombramiento.getNumeroDocumento());
        dto.setCargo(nombramiento.getCargo());
        dto.setActaAprueba(nombramiento.getActaAprueba());
        dto.setFechaActa(nombramiento.getFechaActa());
        dto.setTarjetaProfesional(nombramiento.getTarjetaProfesional());
        dto.setFacultadesLimitaciones(nombramiento.getFacultadesLimitaciones());
        dto.setVigente(nombramiento.getVigente());
        return dto;
    }

    private OrganoAdministracionDto toOrganoAdministracionDto(OrganoAdministracion organo) {
        OrganoAdministracionDto dto = new OrganoAdministracionDto();
        dto.setId(organo.getId());
        dto.setEsalId(organo.getEsalId());
        dto.setOrgano(organo.getOrgano());
        dto.setMiembro(organo.getMiembro());
        dto.setCargo(organo.getCargo());
        dto.setTipoDocumento(organo.getTipoDocumento());
        dto.setNumeroDocumento(organo.getNumeroDocumento());
        dto.setActaAprueba(organo.getActaAprueba());
        dto.setFechaActa(organo.getFechaActa());
        dto.setActaAclaratoria(organo.getActaAclaratoria());
        dto.setFechaActaAclaratoria(organo.getFechaActaAclaratoria());
        dto.setFacultadesLimitaciones(organo.getFacultadesLimitaciones());
        return dto;
    }
}
