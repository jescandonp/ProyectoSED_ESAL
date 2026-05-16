package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto;
import co.gov.bogota.sed.esal.service.PreviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para vista previa del certificado de existencia y representación.
 *
 * GET  /api/certificados/preview/esales/{id}          — vista previa del certificado
 * POST /api/certificados/preview/esales/{id}/validar  — validar si la generación es posible
 *
 * Acceso: ADMINISTRADOR y EXPEDIDOR (configurado en DevSecurityConfig).
 */
@RestController
@RequestMapping("/api/certificados/preview")
@Tag(name = "Certificados", description = "Vista previa y validacion de certificados ESAL")
@SecurityRequirement(name = "BearerAuth")
public class PreviewController {

    private final PreviewService previewService;

    public PreviewController(PreviewService previewService) {
        this.previewService = previewService;
    }

    @GetMapping("/esales/{id}")
    @Operation(summary = "Obtener vista previa del certificado de existencia y representación")
    public ResponseEntity<PreviewCertificadoDto> obtenerPreview(
            @PathVariable Long id,
            Authentication authentication) {

        PreviewCertificadoDto preview = previewService.obtenerPreview(id, authentication);
        return ResponseEntity.ok(preview);
    }

    @PostMapping("/esales/{id}/validar")
    @Operation(summary = "Validar si la generación del certificado está habilitada")
    public ResponseEntity<PreviewCertificadoDto> validar(
            @PathVariable Long id,
            Authentication authentication) {

        PreviewCertificadoDto preview = previewService.validar(id, authentication);
        return ResponseEntity.ok(preview);
    }
}
