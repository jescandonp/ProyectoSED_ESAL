package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.NumeracionCertificado;
import co.gov.bogota.sed.esal.dto.NumeracionDto;
import co.gov.bogota.sed.esal.dto.NumeracionUpdateDto;
import co.gov.bogota.sed.esal.repository.NumeracionCertificadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.Year;

@Service
public class NumeracionService {

    private static final String PREFIJO_DEFAULT = "ESAL";

    private final NumeracionCertificadoRepository numeracionRepository;
    private final AuditoriaService auditoriaService;

    public NumeracionService(NumeracionCertificadoRepository numeracionRepository,
                             AuditoriaService auditoriaService) {
        this.numeracionRepository = numeracionRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public NumeracionDto obtenerActual() {
        int anio = Year.now().getValue();
        NumeracionCertificado n = numeracionRepository.findByAnioAndActivoTrue(anio)
                .orElse(null);
        if (n == null) {
            NumeracionDto dto = new NumeracionDto();
            dto.setPrefijo(PREFIJO_DEFAULT);
            dto.setAnio(anio);
            dto.setUltimoConsecutivo(0L);
            dto.setActivo(false);
            return dto;
        }
        return toDto(n);
    }

    @Transactional
    public NumeracionDto actualizarPrefijo(NumeracionUpdateDto dto, String usuario) {
        int anio = Year.now().getValue();
        NumeracionCertificado n = numeracionRepository.findByAnioAndActivoTrue(anio)
                .orElseGet(() -> crearNueva(anio, dto.getPrefijo()));
        if (dto.getPrefijo() != null && !dto.getPrefijo().trim().isEmpty()) {
            n.setPrefijo(dto.getPrefijo().trim().toUpperCase());
        }
        n.setUpdatedAt(LocalDateTime.now());
        NumeracionCertificado saved = numeracionRepository.save(n);
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.NUMERACION_ACTUALIZADA, AuditoriaAcciones.ENTIDAD_NUMERACION,
                saved.getId(), null, AuditoriaAcciones.RESULTADO_EXITO, "Prefijo: " + saved.getPrefijo());
        return toDto(saved);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String reservarSiguienteNumero(String usuario) {
        int anio = Year.now().getValue();
        NumeracionCertificado n = numeracionRepository.findByAnioAndActivoTrue(anio)
                .orElseGet(() -> crearNueva(anio, PREFIJO_DEFAULT));
        long siguiente = n.getUltimoConsecutivo() + 1;
        n.setUltimoConsecutivo(siguiente);
        n.setUpdatedAt(LocalDateTime.now());
        numeracionRepository.saveAndFlush(n);
        return String.format("%s-%d-%06d", n.getPrefijo(), anio, siguiente);
    }

    private NumeracionCertificado crearNueva(int anio, String prefijo) {
        NumeracionCertificado n = new NumeracionCertificado();
        n.setPrefijo(prefijo != null ? prefijo.trim().toUpperCase() : PREFIJO_DEFAULT);
        n.setAnio(anio);
        n.setUltimoConsecutivo(0L);
        n.setActivo(true);
        n.setCreatedAt(LocalDateTime.now());
        n.setUpdatedAt(LocalDateTime.now());
        return numeracionRepository.saveAndFlush(n);
    }

    private NumeracionDto toDto(NumeracionCertificado n) {
        NumeracionDto dto = new NumeracionDto();
        dto.setId(n.getId());
        dto.setPrefijo(n.getPrefijo());
        dto.setAnio(n.getAnio());
        dto.setUltimoConsecutivo(n.getUltimoConsecutivo());
        dto.setActivo(n.isActivo());
        dto.setUpdatedAt(n.getUpdatedAt());
        return dto;
    }
}
