package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Esal;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.EsalCreateDto;
import co.gov.bogota.sed.esal.dto.EsalDetalleDto;
import co.gov.bogota.sed.esal.dto.EsalResumenDto;
import co.gov.bogota.sed.esal.dto.EsalUpdateDto;
import co.gov.bogota.sed.esal.dto.PageDto;
import co.gov.bogota.sed.esal.repository.EsalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para operaciones CRUD sobre ESALes.
 */
@Service
@Transactional
public class EsalService {

    private final EsalRepository esalRepository;

    public EsalService(EsalRepository esalRepository) {
        this.esalRepository = esalRepository;
    }

    // =========================================================================
    // Listar con filtros y paginación
    // =========================================================================

    @Transactional(readOnly = true)
    public PageDto<EsalResumenDto> listar(int page, int size, String nombre, String idSipej, EstadoEsal estado) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Normalizar filtros vacíos a null
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        EstadoEsal estadoFiltro = estado;

        Page<Esal> pageResult;

        if (nombreFiltro != null && estadoFiltro != null) {
            pageResult = esalRepository.findByNombreContainingIgnoreCaseAndEstado(nombreFiltro, estadoFiltro, pageable);
        } else if (nombreFiltro != null) {
            pageResult = esalRepository.findByNombreContainingIgnoreCase(nombreFiltro, pageable);
        } else if (estadoFiltro != null) {
            pageResult = esalRepository.findByEstado(estadoFiltro, pageable);
        } else {
            pageResult = esalRepository.findAll(pageable);
        }

        List<EsalResumenDto> content = pageResult.getContent().stream()
                .map(this::toResumenDto)
                .collect(Collectors.toList());

        return new PageDto<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages());
    }

    // =========================================================================
    // Obtener detalle
    // =========================================================================

    @Transactional(readOnly = true)
    public EsalDetalleDto obtener(Long id) {
        Esal esal = findOrThrow(id);
        return toDetalleDto(esal);
    }

    // =========================================================================
    // Crear
    // =========================================================================

    public EsalResumenDto crear(EsalCreateDto dto, String creadoPor) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'nombre' es obligatorio.");
        }

        Esal esal = new Esal();
        esal.setNombre(dto.getNombre().trim());
        esal.setIdSipej(dto.getIdSipej());
        esal.setNit(dto.getNit());
        esal.setDomicilio(dto.getDomicilio());
        esal.setCorreoElectronico(dto.getCorreoElectronico());
        esal.setTerminoDuracion(dto.getTerminoDuracion());
        esal.setObjetoSocial(dto.getObjetoSocial());
        esal.setCreatedAt(LocalDateTime.now());
        esal.setCreatedBy(creadoPor);

        Esal saved = esalRepository.save(esal);
        return toResumenDto(saved);
    }

    // =========================================================================
    // Actualizar
    // =========================================================================

    public EsalResumenDto actualizar(Long id, EsalUpdateDto dto, String actualizadoPor) {
        Esal esal = findOrThrow(id);

        if (dto.getNombre() != null && !dto.getNombre().trim().isEmpty()) {
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
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setUpdatedBy(actualizadoPor);

        Esal saved = esalRepository.save(esal);
        return toResumenDto(saved);
    }

    // =========================================================================
    // Cambiar estado
    // =========================================================================

    public EsalResumenDto cambiarEstado(Long id, EstadoEsal nuevoEstado, String usuario) {
        if (nuevoEstado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'estado' es obligatorio.");
        }
        Esal esal = findOrThrow(id);
        esal.setEstado(nuevoEstado);
        esal.setUpdatedAt(LocalDateTime.now());
        esal.setUpdatedBy(usuario);

        Esal saved = esalRepository.save(esal);
        return toResumenDto(saved);
    }

    // =========================================================================
    // Helpers privados
    // =========================================================================

    private Esal findOrThrow(Long id) {
        return esalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "ESAL no encontrada con id: " + id));
    }

    private EsalResumenDto toResumenDto(Esal esal) {
        EsalResumenDto dto = new EsalResumenDto();
        dto.setId(esal.getId());
        dto.setNombre(esal.getNombre());
        dto.setIdSipej(esal.getIdSipej());
        dto.setNit(esal.getNit());
        dto.setDomicilio(esal.getDomicilio());
        dto.setEstado(esal.getEstado());
        dto.setEstadoCompletitud(esal.getEstadoCompletitud());
        dto.setCreatedAt(esal.getCreatedAt());
        return dto;
    }

    private EsalDetalleDto toDetalleDto(Esal esal) {
        EsalDetalleDto dto = new EsalDetalleDto();
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
        dto.setCreatedAt(esal.getCreatedAt());
        dto.setCreatedBy(esal.getCreatedBy());
        dto.setUpdatedAt(esal.getUpdatedAt());
        dto.setUpdatedBy(esal.getUpdatedBy());
        return dto;
    }
}
