package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.BusquedaDetalleDto;
import co.gov.bogota.sed.esal.dto.BusquedaResultadoDto;
import co.gov.bogota.sed.esal.dto.PageDto;
import co.gov.bogota.sed.esal.service.BusquedaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para búsqueda operativa de ESALes.
 *
 * GET /api/busquedas/esales         — búsqueda paginada con filtros
 * GET /api/busquedas/esales/{id}    — detalle completo de una ESAL
 *
 * Acceso: ADMINISTRADOR y EXPEDIDOR (configurado en DevSecurityConfig).
 */
@RestController
@RequestMapping("/api/busquedas")
@Tag(name = "Busqueda", description = "Busqueda operativa de ESALes")
@SecurityRequirement(name = "BearerAuth")
public class BusquedaController {

    private final BusquedaService busquedaService;

    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @GetMapping("/esales")
    @Operation(summary = "Buscar ESALes con filtros dinámicos y paginación")
    public ResponseEntity<PageDto<BusquedaResultadoDto>> buscar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String idSipej,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String nit,
            @RequestParam(required = false) EstadoEsal estado,
            @RequestParam(required = false) EstadoCompletitud estadoCompletitud,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        PageDto<BusquedaResultadoDto> resultado = busquedaService.buscar(
                q, idSipej, nombre, nit, estado, estadoCompletitud, page, size, authentication);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/esales/{id}")
    @Operation(summary = "Obtener detalle completo de una ESAL por ID")
    public ResponseEntity<BusquedaDetalleDto> obtenerDetalle(
            @PathVariable Long id,
            Authentication authentication) {

        BusquedaDetalleDto detalle = busquedaService.obtenerDetalle(id, authentication);
        return ResponseEntity.ok(detalle);
    }
}
