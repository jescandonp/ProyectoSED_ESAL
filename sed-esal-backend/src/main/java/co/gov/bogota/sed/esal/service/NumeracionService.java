package co.gov.bogota.sed.esal.service;

import co.gov.bogota.sed.esal.domain.NumeracionCertificado;
import co.gov.bogota.sed.esal.dto.NumeracionDto;
import co.gov.bogota.sed.esal.dto.NumeracionUpdateDto;
import co.gov.bogota.sed.esal.repository.NumeracionCertificadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class NumeracionService {

    private static final String PREFIJO_INICIAL = "ESAL";

    private final NumeracionCertificadoRepository numeracionRepository;
    private final AuditoriaService auditoriaService;

    public NumeracionService(NumeracionCertificadoRepository numeracionRepository,
                             AuditoriaService auditoriaService) {
        this.numeracionRepository = numeracionRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public NumeracionDto consultar() {
        int anio = LocalDateTime.now().getYear();
        NumeracionCertificado num = numeracionRepository.findByAnioAndActivoTrue(anio)
                .orElseGet(() -> crearNumeracionAnio(anio));
        return toDto(num);
    }

    @Transactional
    public NumeracionDto actualizar(NumeracionUpdateDto dto) {
        int anio = LocalDateTime.now().getYear();
        NumeracionCertificado num = numeracionRepository.findByAnioAndActivoTrue(anio)
                .orElseGet(() -> crearNumeracionAnio(anio));

        if (dto.getPrefijo() != null && !dto.getPrefijo().isBlank()) {
            num.setPrefijo(dto.getPrefijo().toUpperCase().trim());
        }
        num.setUpdatedAt(LocalDateTime.now());
        numeracionRepository.save(num);

        String usuario = obtenerUsuario();
        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.NUMERACION_ACTUALIZADA, "NUMERACION", num.getId(),
                null, AuditoriaAcciones.RESULTADO_EXITO,
                "Prefijo actualizado a: " + num.getPrefijo());

        return toDto(num);
    }

    /**
     * Reserva y retorna el siguiente numero de certificado para el año actual.
     * La transacción que llama a este método debe garantizar aislamiento para evitar duplicados.
     * Se usa un @Transactional pesimista en el llamador (GeneracionService).
     */
    @Transactional
    public String reservarSiguienteNumero() {
        int anio = LocalDateTime.now().getYear();
        NumeracionCertificado num = numeracionRepository.findByAnioAndActivoTrue(anio)
                .orElseGet(() -> crearNumeracionAnio(anio));

        long siguiente = num.getUltimoConsecutivo() + 1;
        num.setUltimoConsecutivo(siguiente);
        num.setUpdatedAt(LocalDateTime.now());
        numeracionRepository.saveAndFlush(num);

        return String.format("%s-%d-%06d", num.getPrefijo(), anio, siguiente);
    }

    private NumeracionCertificado crearNumeracionAnio(int anio) {
        NumeracionCertificado n = new NumeracionCertificado();
        n.setPrefijo(PREFIJO_INICIAL);
        n.setAnio(anio);
        n.setUltimoConsecutivo(0L);
        n.setActivo(true);
        n.setCreatedAt(LocalDateTime.now());
        n.setUpdatedAt(LocalDateTime.now());
        return numeracionRepository.save(n);
    }

    private NumeracionDto toDto(NumeracionCertificado n) {
        NumeracionDto dto = new NumeracionDto();
        dto.setId(n.getId());
        dto.setPrefijo(n.getPrefijo());
        dto.setAnio(n.getAnio());
        dto.setUltimoConsecutivo(n.getUltimoConsecutivo());
        dto.setActivo(n.getActivo());
        dto.setUpdatedAt(n.getUpdatedAt());
        dto.setProximoNumero(String.format("%s-%d-%06d", n.getPrefijo(), n.getAnio(),
                n.getUltimoConsecutivo() + 1));
        return dto;
    }

    private String obtenerUsuario() {
        org.springframework.security.core.Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "sistema";
    }
}
