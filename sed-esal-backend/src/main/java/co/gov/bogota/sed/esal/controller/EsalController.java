package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.CompletitudDto;
import co.gov.bogota.sed.esal.service.CompletitudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para operaciones sobre ESALes.
 *
 * Endpoints de completitud:
 *   GET  /api/esales/{id}/completitud           — ADMINISTRADOR y EXPEDIDOR
 *   POST /api/esales/{id}/completitud/recalcular — solo ADMINISTRADOR
 *
 * La seguridad está cubierta por DevSecurityConfig:
 *   - GET /api/esales/** → hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
 *   - POST /api/esales/** → hasRole("ADMINISTRADOR")
 */
@RestController
@RequestMapping("/api/esales")
@Tag(name = "ESALes", description = "Consulta y gestión de ESALes")
@SecurityRequirement(name = "BearerAuth")
public class EsalController {

    private final CompletitudService completitudService;

    public EsalController(CompletitudService completitudService) {
        this.completitudService = completitudService;
    }

    /**
     * Consulta el semáforo de completitud de una ESAL sin recalcular.
     * Accesible por ADMINISTRADOR y EXPEDIDOR.
     *
     * @param id ID de la ESAL
     * @return DTO con el estado de completitud actual
     */
    @GetMapping("/{id}/completitud")
    @Operation(summary = "Consultar completitud de una ESAL",
               description = "Devuelve el semáforo de completitud y las advertencias actuales sin recalcular.")
    public ResponseEntity<CompletitudDto> getCompletitud(@PathVariable Long id) {
        return ResponseEntity.ok(completitudService.consultar(id));
    }

    /**
     * Recalcula y actualiza el semáforo de completitud de una ESAL.
     * Solo ADMINISTRADOR.
     *
     * @param id ID de la ESAL
     * @return DTO con el resultado del recálculo
     */
    @PostMapping("/{id}/completitud/recalcular")
    @Operation(summary = "Recalcular completitud de una ESAL",
               description = "Evalúa todas las reglas de completitud, actualiza advertencias y semáforo.")
    public ResponseEntity<CompletitudDto> recalcularCompletitud(@PathVariable Long id) {
        return ResponseEntity.ok(completitudService.calcular(id));
    }
}
