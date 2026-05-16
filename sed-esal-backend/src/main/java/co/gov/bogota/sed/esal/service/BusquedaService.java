package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.ActuacionAdministrativa;
import co.gov.bogota.sed.esal.domain.DocumentoSoporte;
import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.Nombramiento;
import co.gov.bogota.sed.esal.domain.OrganoAdministracion;
import co.gov.bogota.sed.esal.domain.PersoneriaJuridica;
import co.gov.bogota.sed.esal.domain.ReformaEstatutaria;
import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.BusquedaDetalleDto;
import co.gov.bogota.sed.esal.dto.BusquedaDetalleDto.ActuacionDto;
import co.gov.bogota.sed.esal.dto.BusquedaDetalleDto.NombramientoDto;
import co.gov.bogota.sed.esal.dto.BusquedaDetalleDto.OrganoDto;
import co.gov.bogota.sed.esal.dto.BusquedaDetalleDto.PersoneriaSeccionDto;
import co.gov.bogota.sed.esal.dto.BusquedaDetalleDto.ReformaDto;
import co.gov.bogota.sed.esal.dto.BusquedaResultadoDto;
import co.gov.bogota.sed.esal.dto.CompletitudDto;
import co.gov.bogota.sed.esal.dto.DocumentoSoporteDto;
import co.gov.bogota.sed.esal.dto.PageDto;
import co.gov.bogota.sed.esal.repository.ActuacionAdministrativaRepository;
import co.gov.bogota.sed.esal.repository.AdvertenciaCompletitudRepository;
import co.gov.bogota.sed.esal.repository.DocumentoSoporteRepository;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import co.gov.bogota.sed.esal.repository.NombramientoRepository;
import co.gov.bogota.sed.esal.repository.OrganoAdministracionRepository;
import co.gov.bogota.sed.esal.repository.PersoneriaJuridicaRepository;
import co.gov.bogota.sed.esal.repository.ReformaEstatutariaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BusquedaService {

    private final EsalRepository esalRepository;
    private final PersoneriaJuridicaRepository personeriaRepository;
    private final ReformaEstatutariaRepository reformaRepository;
    private final NombramientoRepository nombramientoRepository;
    private final OrganoAdministracionRepository organoRepository;
    private final ActuacionAdministrativaRepository actuacionRepository;
    private final DocumentoSoporteRepository documentoRepository;
    private final AdvertenciaCompletitudRepository advertenciaRepository;
    private final CompletitudService completitudService;
    private final AuditoriaService auditoriaService;

    public BusquedaService(
            EsalRepository esalRepository,
            PersoneriaJuridicaRepository personeriaRepository,
            ReformaEstatutariaRepository reformaRepository,
            NombramientoRepository nombramientoRepository,
            OrganoAdministracionRepository organoRepository,
            ActuacionAdministrativaRepository actuacionRepository,
            DocumentoSoporteRepository documentoRepository,
            AdvertenciaCompletitudRepository advertenciaRepository,
            CompletitudService completitudService,
            AuditoriaService auditoriaService) {
        this.esalRepository = esalRepository;
        this.personeriaRepository = personeriaRepository;
        this.reformaRepository = reformaRepository;
        this.nombramientoRepository = nombramientoRepository;
        this.organoRepository = organoRepository;
        this.actuacionRepository = actuacionRepository;
        this.documentoRepository = documentoRepository;
        this.advertenciaRepository = advertenciaRepository;
        this.completitudService = completitudService;
        this.auditoriaService = auditoriaService;
    }

    // -------------------------------------------------------------------------
    // Búsqueda paginada con filtros dinámicos
    // -------------------------------------------------------------------------

    public PageDto<BusquedaResultadoDto> buscar(
            String q,
            String idSipej,
            String nombre,
            String nit,
            EstadoEsal estado,
            EstadoCompletitud estadoCompletitud,
            int page,
            int size,
            Authentication auth) {

        Specification<Esal> spec = construirSpec(q, idSipej, nombre, nit, estado, estadoCompletitud);
        Page<Esal> pageResult = esalRepository.findAll(
                spec, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));

        List<BusquedaResultadoDto> content = pageResult.getContent().stream()
                .map(this::toResultadoDto)
                .collect(Collectors.toList());

        String usuario = auth != null ? auth.getName() : "sistema";
        auditoriaService.registrar(
                usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.BUSQUEDA_ESAL, AuditoriaAcciones.ENTIDAD_ESAL,
                null, null, AuditoriaAcciones.RESULTADO_EXITO,
                "Pagina: " + page + ", Total: " + pageResult.getTotalElements());

        return new PageDto<>(content, pageResult.getNumber(), pageResult.getSize(),
                pageResult.getTotalElements(), pageResult.getTotalPages());
    }

    // -------------------------------------------------------------------------
    // Detalle completo de una ESAL
    // -------------------------------------------------------------------------

    public BusquedaDetalleDto obtenerDetalle(Long esalId, Authentication auth) {
        Esal esal = esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));

        BusquedaDetalleDto dto = new BusquedaDetalleDto();
        dto.setEsalId(esal.getId());
        dto.setNombre(esal.getNombre());
        dto.setIdSipej(esal.getIdSipej());
        dto.setNit(esal.getNit());
        dto.setDomicilio(esal.getDomicilio());
        dto.setCorreoElectronico(esal.getCorreoElectronico());
        dto.setTerminoDuracion(esal.getTerminoDuracion());
        dto.setObjetoSocial(esal.getObjetoSocial());
        dto.setEstado(esal.getEstado());
        dto.setEstadoCompletitud(esal.getEstadoCompletitud());
        dto.setUpdatedAt(esal.getUpdatedAt());

        // Personería
        List<PersoneriaJuridica> pjs = personeriaRepository.findByEsalId(esalId);
        if (!pjs.isEmpty()) {
            dto.setPersoneria(toPersoneriaDto(pjs.get(0)));
        }

        // Reformas
        dto.setReformas(reformaRepository.findByEsalIdOrderByOrden(esalId)
                .stream().map(this::toReformaDto).collect(Collectors.toList()));

        // Nombramientos
        dto.setNombramientos(nombramientoRepository.findByEsalId(esalId)
                .stream().map(this::toNombramientoDto).collect(Collectors.toList()));

        // Órganos
        dto.setOrganos(organoRepository.findByEsalId(esalId)
                .stream().map(this::toOrganoDto).collect(Collectors.toList()));

        // Actuaciones
        dto.setActuaciones(actuacionRepository.findByEsalId(esalId)
                .stream().map(this::toActuacionDto).collect(Collectors.toList()));

        // Documentos
        dto.setDocumentos(documentoRepository.findByEsalId(esalId)
                .stream().map(this::toDocumentoDto).collect(Collectors.toList()));

        // Completitud (sin recalcular)
        CompletitudDto completitud = completitudService.consultar(esalId);
        dto.setCompletitud(completitud);

        String usuario = auth != null ? auth.getName() : "sistema";
        auditoriaService.registrar(
                usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.DETALLE_ESAL_CONSULTADO, AuditoriaAcciones.ENTIDAD_ESAL,
                esalId, esal.getIdSipej(), AuditoriaAcciones.RESULTADO_EXITO, null);

        return dto;
    }

    // -------------------------------------------------------------------------
    // Specification builder
    // -------------------------------------------------------------------------

    private Specification<Esal> construirSpec(
            String q, String idSipej, String nombre, String nit,
            EstadoEsal estado, EstadoCompletitud estadoCompletitud) {

        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();

            // q aplica a nombre, idSipej y nit con OR
            if (q != null && !q.trim().isEmpty()) {
                String patron = "%" + q.trim().toLowerCase() + "%";
                predicados.add(cb.or(
                        cb.like(cb.lower(root.get("nombre")), patron),
                        cb.like(cb.lower(root.get("idSipej")), patron),
                        cb.like(cb.lower(root.get("nit")), patron)
                ));
            }

            if (idSipej != null && !idSipej.trim().isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("idSipej")),
                        "%" + idSipej.trim().toLowerCase() + "%"));
            }

            if (nombre != null && !nombre.trim().isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("nombre")),
                        "%" + nombre.trim().toLowerCase() + "%"));
            }

            if (nit != null && !nit.trim().isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("nit")),
                        "%" + nit.trim().toLowerCase() + "%"));
            }

            if (estado != null) {
                predicados.add(cb.equal(root.get("estado"), estado));
            }

            if (estadoCompletitud != null) {
                predicados.add(cb.equal(root.get("estadoCompletitud"), estadoCompletitud));
            }

            return predicados.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicados.toArray(new Predicate[0]));
        };
    }

    // -------------------------------------------------------------------------
    // Mappers
    // -------------------------------------------------------------------------

    private BusquedaResultadoDto toResultadoDto(Esal e) {
        BusquedaResultadoDto dto = new BusquedaResultadoDto();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setIdSipej(e.getIdSipej());
        dto.setNit(e.getNit());
        dto.setDomicilio(e.getDomicilio());
        dto.setEstado(e.getEstado());
        dto.setEstadoCompletitud(e.getEstadoCompletitud());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    private PersoneriaSeccionDto toPersoneriaDto(PersoneriaJuridica pj) {
        PersoneriaSeccionDto dto = new PersoneriaSeccionDto();
        dto.setReconocimiento(pj.getReconocimientoPersoneriaJuridica());
        dto.setFechaReconocimiento(pj.getFechaReconocimientoPersoneriaJuridica());
        dto.setEntidadQueExpide(pj.getEntidadQueExpide());
        dto.setInscripcion(pj.getInscripcion());
        dto.setFechaInscripcion(pj.getFechaInscripcion());
        dto.setEntidadQueInscribio(pj.getEntidadQueInscribio());
        return dto;
    }

    private ReformaDto toReformaDto(ReformaEstatutaria r) {
        ReformaDto dto = new ReformaDto();
        dto.setOrden(r.getOrden());
        dto.setTipoActo(r.getTipoActo());
        dto.setNumeroActo(r.getNumeroActo());
        dto.setFechaActo(r.getFechaActo());
        dto.setEntidadQueExpide(r.getEntidadQueExpide());
        dto.setDescripcion(r.getDescripcion());
        return dto;
    }

    private NombramientoDto toNombramientoDto(Nombramiento n) {
        NombramientoDto dto = new NombramientoDto();
        dto.setTipoNombramiento(n.getTipoNombramiento() != null ? n.getTipoNombramiento().name() : null);
        dto.setNombre(n.getNombre());
        dto.setTipoDocumento(n.getTipoDocumento());
        dto.setNumeroDocumento(n.getNumeroDocumento());
        dto.setCargo(n.getCargo());
        dto.setActaAprueba(n.getActaAprueba());
        dto.setFechaActa(n.getFechaActa());
        dto.setFacultadesLimitaciones(n.getFacultadesLimitaciones());
        return dto;
    }

    private OrganoDto toOrganoDto(OrganoAdministracion o) {
        OrganoDto dto = new OrganoDto();
        dto.setOrgano(o.getOrgano());
        dto.setMiembro(o.getMiembro());
        dto.setCargo(o.getCargo());
        dto.setActaAprueba(o.getActaAprueba());
        dto.setFechaActa(o.getFechaActa());
        return dto;
    }

    private ActuacionDto toActuacionDto(ActuacionAdministrativa a) {
        ActuacionDto dto = new ActuacionDto();
        dto.setTipoActuacion(a.getTipoActuacion() != null ? a.getTipoActuacion().name() : null);
        dto.setActa(a.getActa());
        dto.setFechaActa(a.getFechaActa());
        dto.setResolucion(a.getResolucion());
        dto.setFechaResolucion(a.getFechaResolucion());
        dto.setMotivo(a.getMotivo());
        dto.setTiempoSuspension(a.getTiempoSuspension());
        dto.setFechaInicio(a.getFechaInicio());
        return dto;
    }

    private DocumentoSoporteDto toDocumentoDto(DocumentoSoporte d) {
        DocumentoSoporteDto dto = new DocumentoSoporteDto();
        dto.setId(d.getId());
        dto.setEsalId(d.getEsalId());
        dto.setTipoProceso(d.getTipoProceso());
        dto.setTipoDocumento(d.getTipoDocumento());
        dto.setNombreArchivo(d.getNombreArchivo());
        dto.setContentType(d.getContentType());
        dto.setTamanoBytes(d.getTamanoBytes());
        dto.setEstadoValidacion(d.getEstadoValidacion());
        dto.setCreatedAt(d.getCreatedAt());
        dto.setCreatedBy(d.getCreatedBy());
        return dto;
    }
}
