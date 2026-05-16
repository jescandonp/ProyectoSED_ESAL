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
import co.gov.bogota.sed.esal.dto.BusquedaResultadoDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BusquedaService {

    private final EsalRepository esalRepository;
    private final PersoneriaJuridicaRepository personeriaRepository;
    private final NombramientoRepository nombramientoRepository;
    private final OrganoAdministracionRepository organoRepository;
    private final ActuacionAdministrativaRepository actuacionRepository;
    private final ReformaEstatutariaRepository reformaRepository;
    private final DocumentoSoporteRepository documentoRepository;
    private final AdvertenciaCompletitudRepository advertenciaRepository;
    private final CompletitudService completitudService;
    private final AuditoriaService auditoriaService;

    public BusquedaService(EsalRepository esalRepository,
                           PersoneriaJuridicaRepository personeriaRepository,
                           NombramientoRepository nombramientoRepository,
                           OrganoAdministracionRepository organoRepository,
                           ActuacionAdministrativaRepository actuacionRepository,
                           ReformaEstatutariaRepository reformaRepository,
                           DocumentoSoporteRepository documentoRepository,
                           AdvertenciaCompletitudRepository advertenciaRepository,
                           CompletitudService completitudService,
                           AuditoriaService auditoriaService) {
        this.esalRepository = esalRepository;
        this.personeriaRepository = personeriaRepository;
        this.nombramientoRepository = nombramientoRepository;
        this.organoRepository = organoRepository;
        this.actuacionRepository = actuacionRepository;
        this.reformaRepository = reformaRepository;
        this.documentoRepository = documentoRepository;
        this.advertenciaRepository = advertenciaRepository;
        this.completitudService = completitudService;
        this.auditoriaService = auditoriaService;
    }

    public PageDto<BusquedaResultadoDto> buscar(String q, String idSipej, String nombre, String nit,
                                                EstadoEsal estado,
                                                EstadoCompletitud estadoCompletitud,
                                                int page, int size, String usuario) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt", "createdAt"));
        Specification<Esal> specification = construirSpecification(q, idSipej, nombre, nit, estado, estadoCompletitud);
        Page<Esal> resultado = esalRepository.findAll(specification, pageable);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.BUSQUEDA_ESAL,
                AuditoriaAcciones.ENTIDAD_ESAL,
                null, null,
                AuditoriaAcciones.RESULTADO_EXITO,
                "Resultados: " + resultado.getTotalElements());

        List<BusquedaResultadoDto> content = resultado.getContent().stream()
                .map(this::toBusquedaResultadoDto)
                .collect(Collectors.toList());
        return new PageDto<>(content, resultado.getNumber(), resultado.getSize(),
                resultado.getTotalElements(), resultado.getTotalPages());
    }

    public BusquedaDetalleDto obtenerDetalle(Long esalId, String usuario) {
        Esal esal = esalRepository.findById(esalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + esalId));

        List<PersoneriaJuridica> personerias = personeriaRepository.findByEsalId(esalId);
        List<ReformaEstatutaria> reformas = reformaRepository.findByEsalIdOrderByOrden(esalId);
        List<Nombramiento> nombramientos = nombramientoRepository.findByEsalId(esalId);
        List<OrganoAdministracion> organos = organoRepository.findByEsalId(esalId);
        List<ActuacionAdministrativa> actuaciones = actuacionRepository.findByEsalId(esalId);
        List<DocumentoSoporte> documentos = documentoRepository.findByEsalId(esalId);
        advertenciaRepository.findByEsalId(esalId);

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
        dto.setPersoneria(personerias.isEmpty() ? null : toPersoneriaDto(personerias.get(0)));
        dto.setReformas(reformas.stream().map(this::toReformaDto).collect(Collectors.toList()));
        dto.setNombramientos(nombramientos.stream().map(this::toNombramientoDto).collect(Collectors.toList()));
        dto.setOrganos(organos.stream().map(this::toOrganoDto).collect(Collectors.toList()));
        dto.setActuaciones(actuaciones.stream().map(this::toActuacionDto).collect(Collectors.toList()));
        dto.setDocumentos(documentos.stream().map(this::toDocumentoDto).collect(Collectors.toList()));
        dto.setCompletitud(completitudService.consultar(esalId));
        dto.setUpdatedAt(esal.getUpdatedAt());

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.DETALLE_ESAL_CONSULTADO,
                AuditoriaAcciones.ENTIDAD_ESAL,
                esal.getId(), esal.getIdSipej(),
                AuditoriaAcciones.RESULTADO_EXITO, null);

        return dto;
    }

    private Specification<Esal> construirSpecification(String q, String idSipej, String nombre, String nit,
                                                       EstadoEsal estado,
                                                       EstadoCompletitud estadoCompletitud) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String qFiltro = normalizar(q);
            if (qFiltro != null) {
                String like = like(qFiltro);
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nombre")), like),
                        cb.like(cb.lower(root.get("idSipej")), like),
                        cb.like(cb.lower(root.get("nit")), like)));
            }
            agregarLike(predicates, cb, root.get("idSipej"), idSipej);
            agregarLike(predicates, cb, root.get("nombre"), nombre);
            agregarLike(predicates, cb, root.get("nit"), nit);
            if (estado != null) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }
            if (estadoCompletitud != null) {
                predicates.add(cb.equal(root.get("estadoCompletitud"), estadoCompletitud));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void agregarLike(List<Predicate> predicates, javax.persistence.criteria.CriteriaBuilder cb,
                             javax.persistence.criteria.Path<String> path, String valor) {
        String filtro = normalizar(valor);
        if (filtro != null) {
            predicates.add(cb.like(cb.lower(path), like(filtro)));
        }
    }

    private String normalizar(String valor) {
        return valor == null || valor.trim().isEmpty() ? null : valor.trim().toLowerCase(Locale.ROOT);
    }

    private String like(String valor) {
        return "%" + valor + "%";
    }

    private BusquedaResultadoDto toBusquedaResultadoDto(Esal esal) {
        BusquedaResultadoDto dto = new BusquedaResultadoDto();
        dto.setId(esal.getId());
        dto.setNombre(esal.getNombre());
        dto.setIdSipej(esal.getIdSipej());
        dto.setNit(esal.getNit());
        dto.setDomicilio(esal.getDomicilio());
        dto.setEstado(esal.getEstado());
        dto.setEstadoCompletitud(esal.getEstadoCompletitud());
        dto.setUpdatedAt(esal.getUpdatedAt());
        return dto;
    }

    private BusquedaDetalleDto.PersoneriaSeccionDto toPersoneriaDto(PersoneriaJuridica personeria) {
        BusquedaDetalleDto.PersoneriaSeccionDto dto = new BusquedaDetalleDto.PersoneriaSeccionDto();
        dto.setId(personeria.getId());
        dto.setReconocimientoPersoneriaJuridica(personeria.getReconocimientoPersoneriaJuridica());
        dto.setFechaReconocimientoPersoneriaJuridica(personeria.getFechaReconocimientoPersoneriaJuridica());
        dto.setEntidadQueExpide(personeria.getEntidadQueExpide());
        dto.setInscripcion(personeria.getInscripcion());
        dto.setFechaInscripcion(personeria.getFechaInscripcion());
        dto.setEntidadQueInscribio(personeria.getEntidadQueInscribio());
        return dto;
    }

    private BusquedaDetalleDto.ReformaDto toReformaDto(ReformaEstatutaria reforma) {
        BusquedaDetalleDto.ReformaDto dto = new BusquedaDetalleDto.ReformaDto();
        dto.setId(reforma.getId());
        dto.setOrden(reforma.getOrden());
        dto.setTipoActo(reforma.getTipoActo());
        dto.setNumeroActo(reforma.getNumeroActo());
        dto.setFechaActo(reforma.getFechaActo());
        dto.setEntidadQueExpide(reforma.getEntidadQueExpide());
        dto.setDescripcion(reforma.getDescripcion());
        return dto;
    }

    private BusquedaDetalleDto.NombramientoDto toNombramientoDto(Nombramiento nombramiento) {
        BusquedaDetalleDto.NombramientoDto dto = new BusquedaDetalleDto.NombramientoDto();
        dto.setId(nombramiento.getId());
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

    private BusquedaDetalleDto.OrganoDto toOrganoDto(OrganoAdministracion organo) {
        BusquedaDetalleDto.OrganoDto dto = new BusquedaDetalleDto.OrganoDto();
        dto.setId(organo.getId());
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

    private BusquedaDetalleDto.ActuacionDto toActuacionDto(ActuacionAdministrativa actuacion) {
        BusquedaDetalleDto.ActuacionDto dto = new BusquedaDetalleDto.ActuacionDto();
        dto.setId(actuacion.getId());
        dto.setTipoActuacion(actuacion.getTipoActuacion());
        dto.setActa(actuacion.getActa());
        dto.setFechaActa(actuacion.getFechaActa());
        dto.setResolucion(actuacion.getResolucion());
        dto.setFechaResolucion(actuacion.getFechaResolucion());
        dto.setMotivo(actuacion.getMotivo());
        dto.setTiempoSuspension(actuacion.getTiempoSuspension());
        dto.setFechaInicio(actuacion.getFechaInicio());
        dto.setFechaFin(actuacion.getFechaFin());
        return dto;
    }

    private DocumentoSoporteDto toDocumentoDto(DocumentoSoporte documento) {
        DocumentoSoporteDto dto = new DocumentoSoporteDto();
        dto.setId(documento.getId());
        dto.setEsalId(documento.getEsalId());
        dto.setTipoProceso(documento.getTipoProceso());
        dto.setTipoDocumento(documento.getTipoDocumento());
        dto.setNombreArchivo(documento.getNombreArchivo());
        dto.setContentType(documento.getContentType());
        dto.setTamanoBytes(documento.getTamanoBytes());
        dto.setEstadoValidacion(documento.getEstadoValidacion());
        dto.setCreatedAt(documento.getCreatedAt());
        dto.setCreatedBy(documento.getCreatedBy());
        return dto;
    }
}
