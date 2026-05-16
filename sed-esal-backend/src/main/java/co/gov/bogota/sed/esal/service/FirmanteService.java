package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.Firmante;
import co.gov.bogota.sed.esal.dto.FirmanteCreateDto;
import co.gov.bogota.sed.esal.dto.FirmanteDto;
import co.gov.bogota.sed.esal.repository.FirmanteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FirmanteService {

    private final FirmanteRepository firmanteRepository;
    private final AuditoriaService auditoriaService;

    public FirmanteService(FirmanteRepository firmanteRepository,
                           AuditoriaService auditoriaService) {
        this.firmanteRepository = firmanteRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<FirmanteDto> listar() {
        return firmanteRepository.findAllByOrderByFechaInicioVigenciaDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public FirmanteDto obtener(Long id) {
        return toDto(buscarOFallar(id));
    }

    @Transactional
    public FirmanteDto crear(FirmanteCreateDto dto) {
        validarCamposObligatorios(dto);
        LocalDate inicio = dto.getFechaInicioVigencia();
        LocalDate fin = dto.getFechaFinVigencia() != null ? dto.getFechaFinVigencia() : LocalDate.of(9999, 12, 31);
        validarSolapamiento(inicio, fin, null);

        Firmante f = new Firmante();
        f.setNombre(dto.getNombre().trim());
        f.setCargo(dto.getCargo().trim());
        f.setDependencia(dto.getDependencia() != null ? dto.getDependencia().trim() : null);
        f.setFechaInicioVigencia(inicio);
        f.setFechaFinVigencia(dto.getFechaFinVigencia());
        f.setActivo(true);
        f.setCreatedAt(LocalDateTime.now());
        f.setCreatedBy(obtenerUsuario());
        f.setUpdatedAt(LocalDateTime.now());
        firmanteRepository.save(f);

        auditoriaService.registrar(f.getCreatedBy(), auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.FIRMANTE_CREADO, AuditoriaAcciones.ENTIDAD_FIRMANTE,
                f.getId(), null, AuditoriaAcciones.RESULTADO_EXITO,
                f.getNombre() + " — " + f.getCargo());

        return toDto(f);
    }

    @Transactional
    public FirmanteDto actualizar(Long id, FirmanteCreateDto dto) {
        Firmante f = buscarOFallar(id);
        validarCamposObligatorios(dto);

        LocalDate inicio = dto.getFechaInicioVigencia();
        LocalDate fin = dto.getFechaFinVigencia() != null ? dto.getFechaFinVigencia() : LocalDate.of(9999, 12, 31);
        if (f.getActivo()) {
            validarSolapamiento(inicio, fin, id);
        }

        f.setNombre(dto.getNombre().trim());
        f.setCargo(dto.getCargo().trim());
        f.setDependencia(dto.getDependencia() != null ? dto.getDependencia().trim() : null);
        f.setFechaInicioVigencia(inicio);
        f.setFechaFinVigencia(dto.getFechaFinVigencia());
        f.setUpdatedAt(LocalDateTime.now());
        f.setUpdatedBy(obtenerUsuario());
        firmanteRepository.save(f);

        auditoriaService.registrar(f.getUpdatedBy(), auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.FIRMANTE_ACTUALIZADO, AuditoriaAcciones.ENTIDAD_FIRMANTE,
                f.getId(), null, AuditoriaAcciones.RESULTADO_EXITO, f.getNombre());

        return toDto(f);
    }

    @Transactional
    public FirmanteDto activar(Long id) {
        Firmante f = buscarOFallar(id);
        LocalDate inicio = f.getFechaInicioVigencia();
        LocalDate fin = f.getFechaFinVigencia() != null ? f.getFechaFinVigencia() : LocalDate.of(9999, 12, 31);
        validarSolapamiento(inicio, fin, id);
        f.setActivo(true);
        f.setUpdatedAt(LocalDateTime.now());
        f.setUpdatedBy(obtenerUsuario());
        firmanteRepository.save(f);

        auditoriaService.registrar(f.getUpdatedBy(), auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.FIRMANTE_ACTIVADO, AuditoriaAcciones.ENTIDAD_FIRMANTE,
                f.getId(), null, AuditoriaAcciones.RESULTADO_EXITO, f.getNombre());

        return toDto(f);
    }

    @Transactional
    public FirmanteDto inactivar(Long id) {
        Firmante f = buscarOFallar(id);
        f.setActivo(false);
        f.setUpdatedAt(LocalDateTime.now());
        f.setUpdatedBy(obtenerUsuario());
        firmanteRepository.save(f);

        auditoriaService.registrar(f.getUpdatedBy(), auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.FIRMANTE_INACTIVADO, AuditoriaAcciones.ENTIDAD_FIRMANTE,
                f.getId(), null, AuditoriaAcciones.RESULTADO_EXITO, f.getNombre());

        return toDto(f);
    }

    /**
     * Retorna el firmante vigente para la fecha de expedición dada.
     * Lanza 422 si no existe o hay más de uno (solapamiento).
     */
    public Firmante resolverFirmanteVigente(LocalDate fecha) {
        List<Firmante> vigentes = firmanteRepository.findVigentesEn(fecha);
        if (vigentes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "No existe firmante vigente para la fecha " + fecha +
                    ". Configure un firmante activo antes de generar certificados.");
        }
        if (vigentes.size() > 1) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Existe más de un firmante vigente para la fecha " + fecha +
                    ". Resuelva el solapamiento antes de generar certificados.");
        }
        return vigentes.get(0);
    }

    // -------------------------------------------------------------------------
    // Helpers internos
    // -------------------------------------------------------------------------

    private void validarCamposObligatorios(FirmanteCreateDto dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del firmante es obligatorio.");
        }
        if (dto.getCargo() == null || dto.getCargo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cargo del firmante es obligatorio.");
        }
        if (dto.getFechaInicioVigencia() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de inicio de vigencia es obligatoria.");
        }
        if (dto.getFechaFinVigencia() != null &&
                !dto.getFechaFinVigencia().isAfter(dto.getFechaInicioVigencia())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de fin de vigencia debe ser posterior a la fecha de inicio.");
        }
    }

    private void validarSolapamiento(LocalDate inicio, LocalDate fin, Long excludeId) {
        List<Firmante> solapados = excludeId != null
                ? firmanteRepository.findSolapados(inicio, fin, excludeId)
                : firmanteRepository.findSolapadosSinExcluir(inicio, fin);
        if (!solapados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Las vigencias se solapan con el firmante: " + solapados.get(0).getNombre() +
                    " (vigencia: " + solapados.get(0).getFechaInicioVigencia() + " — " +
                    solapados.get(0).getFechaFinVigencia() + "). " +
                    "Inactívelo o ajuste las fechas antes de continuar.");
        }
    }

    private Firmante buscarOFallar(Long id) {
        return firmanteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Firmante no encontrado: " + id));
    }

    private FirmanteDto toDto(Firmante f) {
        FirmanteDto dto = new FirmanteDto();
        dto.setId(f.getId());
        dto.setNombre(f.getNombre());
        dto.setCargo(f.getCargo());
        dto.setDependencia(f.getDependencia());
        dto.setFechaInicioVigencia(f.getFechaInicioVigencia());
        dto.setFechaFinVigencia(f.getFechaFinVigencia());
        dto.setActivo(f.getActivo());
        dto.setCreatedAt(f.getCreatedAt());
        dto.setCreatedBy(f.getCreatedBy());
        dto.setUpdatedAt(f.getUpdatedAt());
        return dto;
    }

    private String obtenerUsuario() {
        org.springframework.security.core.Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "sistema";
    }
}
