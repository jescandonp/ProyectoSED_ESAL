package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.domain.Auditoria;
import co.gov.bogota.sed.esal.dto.AuditoriaDto;
import co.gov.bogota.sed.esal.dto.PageDto;
import co.gov.bogota.sed.esal.repository.AuditoriaRepository;
import co.gov.bogota.sed.esal.service.AuditoriaAcciones;
import co.gov.bogota.sed.esal.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para consulta de registros de auditoría.
 * Solo accesible por ADMINISTRADOR (protegido por DevSecurityConfig).
 */
@RestController
@RequestMapping("/api/admin/auditoria")
@Tag(name = "Auditoría", description = "Consulta de registros de auditoría del sistema")
@SecurityRequirement(name = "BearerAuth")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaRepository auditoriaRepository,
                                AuditoriaService auditoriaService) {
        this.auditoriaRepository = auditoriaRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Lista registros de auditoría paginados, ordenados por fecha descendente.
     * Solo ADMINISTRADOR.
     *
     * @param page número de página (0-based, default 0)
     * @param size tamaño de página (default 20)
     * @return página de AuditoriaDto
     */
    @GetMapping
    @Operation(summary = "Listar registros de auditoría",
               description = "Devuelve una página de registros de auditoría ordenados por fecha descendente. Solo ADMINISTRADOR.")
    public ResponseEntity<PageDto<AuditoriaDto>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Auditoria> pageResult = auditoriaRepository.findAll(pageable);

        auditoriaService.registrar(usuario, auditoriaService.obtenerRolActual(),
                AuditoriaAcciones.CONSULTAR_AUDITORIA,
                AuditoriaAcciones.ENTIDAD_ESAL,
                null, null,
                AuditoriaAcciones.RESULTADO_EXITO,
                "Pagina: " + page + ", Total: " + pageResult.getTotalElements());

        List<AuditoriaDto> content = pageResult.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        PageDto<AuditoriaDto> result = new PageDto<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages());

        return ResponseEntity.ok(result);
    }

    private AuditoriaDto toDto(Auditoria a) {
        AuditoriaDto dto = new AuditoriaDto();
        dto.setId(a.getId());
        dto.setUsuario(a.getUsuario());
        dto.setRol(a.getRol());
        dto.setAccion(a.getAccion());
        dto.setEntidad(a.getEntidad());
        dto.setEntidadId(a.getEntidadId());
        dto.setIdSipej(a.getIdSipej());
        dto.setResultado(a.getResultado());
        dto.setDetalle(a.getDetalle());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}
