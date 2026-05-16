package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Firmante;
import co.gov.bogota.sed.esal.dto.FirmanteCreateDto;
import co.gov.bogota.sed.esal.dto.FirmanteDto;
import co.gov.bogota.sed.esal.repository.FirmanteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FirmanteService {

    private final FirmanteRepository firmanteRepository;
    private final AuditoriaService auditoriaService;

    public FirmanteService(FirmanteRepository firmanteRepository,
                           AuditoriaService auditoriaService) {
        this.firmanteRepository = firmanteRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<FirmanteDto> listar() {
        return firmanteRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public FirmanteDto crear(FirmanteCreateDto dto, String usuario) {
        validarCamposObligatorios(dto);
        LocalDate fin = dto.getFechaFinVigencia() != null ? dto.getFechaFinVigencia() : LocalDate.of(9999, 12, 31);
        List<Firmante> solapados = firmanteRepository.findSolapadosNew(dto.getFechaInicioVigencia(), fin);
        if (!solapados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un firmante activo con vigencia solapada en ese periodo.");
        }
        Firmante f = new Firmante();
        f.setNombre(dto.getNombre().trim());
        f.setCargo(dto.getCargo().trim());
        f.setDependencia(dto.getDependencia() != null ? dto.getDependencia().trim() : null);
        f.setFechaInicioVigencia(dto.getFechaInicioVigencia());
        f.setFechaFinVigencia(dto.getFechaFinVigencia());
        f.setActivo(true);
        f.setCreatedAt(LocalDateTime.now());
        f.setCreatedBy(usuario);
        f.setUpdatedAt(LocalDateTime.now());
        f.setUpdatedBy(usuario);
        Firmante saved = firmanteRepository.save(f);
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.FIRMANTE_CREADO, AuditoriaAcciones.ENTIDAD_FIRMANTE,
                saved.getId(), null, AuditoriaAcciones.RESULTADO_EXITO, saved.getNombre());
        return toDto(saved);
    }

    @Transactional
    public FirmanteDto actualizar(Long id, FirmanteCreateDto dto, String usuario) {
        Firmante f = firmanteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Firmante no encontrado."));
        validarCamposObligatorios(dto);
        LocalDate fin = dto.getFechaFinVigencia() != null ? dto.getFechaFinVigencia() : LocalDate.of(9999, 12, 31);
        List<Firmante> solapados = firmanteRepository.findSolapados(dto.getFechaInicioVigencia(), fin, id);
        if (!solapados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe otro firmante activo con vigencia solapada en ese periodo.");
        }
        f.setNombre(dto.getNombre().trim());
        f.setCargo(dto.getCargo().trim());
        f.setDependencia(dto.getDependencia() != null ? dto.getDependencia().trim() : null);
        f.setFechaInicioVigencia(dto.getFechaInicioVigencia());
        f.setFechaFinVigencia(dto.getFechaFinVigencia());
        f.setUpdatedAt(LocalDateTime.now());
        f.setUpdatedBy(usuario);
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.FIRMANTE_ACTUALIZADO, AuditoriaAcciones.ENTIDAD_FIRMANTE,
                id, null, AuditoriaAcciones.RESULTADO_EXITO, f.getNombre());
        return toDto(firmanteRepository.save(f));
    }

    @Transactional
    public FirmanteDto activar(Long id, String usuario) {
        Firmante f = firmanteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Firmante no encontrado."));
        f.setActivo(true);
        f.setUpdatedAt(LocalDateTime.now());
        f.setUpdatedBy(usuario);
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.FIRMANTE_ACTIVADO, AuditoriaAcciones.ENTIDAD_FIRMANTE,
                id, null, AuditoriaAcciones.RESULTADO_EXITO, f.getNombre());
        return toDto(firmanteRepository.save(f));
    }

    @Transactional
    public FirmanteDto inactivar(Long id, String usuario) {
        Firmante f = firmanteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Firmante no encontrado."));
        f.setActivo(false);
        f.setUpdatedAt(LocalDateTime.now());
        f.setUpdatedBy(usuario);
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.FIRMANTE_INACTIVADO, AuditoriaAcciones.ENTIDAD_FIRMANTE,
                id, null, AuditoriaAcciones.RESULTADO_EXITO, f.getNombre());
        return toDto(firmanteRepository.save(f));
    }

    public Firmante resolverVigente(LocalDate fecha) {
        List<Firmante> vigentes = firmanteRepository.findVigentesEnFecha(fecha);
        if (vigentes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "No existe firmante vigente para la fecha de expedicion.");
        }
        return vigentes.get(0);
    }

    private void validarCamposObligatorios(FirmanteCreateDto dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del firmante es obligatorio.");
        }
        if (dto.getCargo() == null || dto.getCargo().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cargo del firmante es obligatorio.");
        }
        if (dto.getFechaInicioVigencia() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de inicio de vigencia es obligatoria.");
        }
    }

    private FirmanteDto toDto(Firmante f) {
        FirmanteDto dto = new FirmanteDto();
        dto.setId(f.getId());
        dto.setNombre(f.getNombre());
        dto.setCargo(f.getCargo());
        dto.setDependencia(f.getDependencia());
        dto.setFechaInicioVigencia(f.getFechaInicioVigencia());
        dto.setFechaFinVigencia(f.getFechaFinVigencia());
        dto.setActivo(f.isActivo());
        dto.setCreatedAt(f.getCreatedAt());
        dto.setCreatedBy(f.getCreatedBy());
        return dto;
    }
}
