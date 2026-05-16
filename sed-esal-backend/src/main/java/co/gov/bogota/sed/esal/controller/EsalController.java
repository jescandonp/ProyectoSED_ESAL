package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.CompletitudDto;
import co.gov.bogota.sed.esal.dto.DocumentoSoporteDto;
import co.gov.bogota.sed.esal.service.CompletitudService;
import co.gov.bogota.sed.esal.service.DocumentoSoporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controlador REST para operaciones sobre ESALes.
 *
 * Endpoints de completitud:
 *   GET  /api/esales/{id}/completitud            - ADMINISTRADOR y EXPEDIDOR
 *   POST /api/esales/{id}/completitud/recalcular - solo ADMINISTRADOR
 *
 * Endpoints de documentos soporte:
 *   POST /api/esales/{id}/documentos             - solo ADMINISTRADOR
 *   GET  /api/esales/{id}/documentos             - ADMINISTRADOR y EXPEDIDOR
 *
 * La seguridad esta cubierta por DevSecurityConfig:
 *   - GET  /api/esales/**                  hasAnyRole("ADMINISTRADOR", "EXPEDIDOR")
 *   - POST /api/esales/{id}/documentos     hasRole("ADMINISTRADOR")
 *   - PUT  /api/esales/**                  hasRole("ADMINISTRADOR")
 */
@RestController
@RequestMapping("/api/esales")
@Tag(name = "ESALes", description = "Consulta y gestion de ESALes")
@SecurityRequirement(name = "BearerAuth")
public class EsalController {

    private final CompletitudService completitudService;
    private final DocumentoSoporteService documentoSoporteService;

    public EsalController(CompletitudService completitudService,
                          DocumentoSoporteService documentoSoporteService) {
        this.completitudService = completitudService;
        this.documentoSoporteService = documentoSoporteService;
    }

    // =========================================================================
    // Completitud
    // =========================================================================

    /**
     * Consulta el semaforo de completitud de una ESAL sin recalcular.
     * Accesible por ADMINISTRADOR y EXPEDIDOR.
     *
     * @param id ID de la ESAL
     * @return DTO con el estado de completitud actual
     */
    @GetMapping("/{id}/completitud")
    @Operation(summary = "Consultar completitud de una ESAL",
               description = "Devuelve el semaforo de completitud y las advertencias actuales sin recalcular.")
    public ResponseEntity<CompletitudDto> getCompletitud(@PathVariable Long id) {
        return ResponseEntity.ok(completitudService.consultar(id));
    }

    /**
     * Recalcula y actualiza el semaforo de completitud de una ESAL.
     * Solo ADMINISTRADOR.
     *
     * @param id ID de la ESAL
     * @return DTO con el resultado del recalculo
     */
    @PostMapping("/{id}/completitud/recalcular")
    @Operation(summary = "Recalcular completitud de una ESAL",
               description = "Evalua todas las reglas de completitud, actualiza advertencias y semaforo.")
    public ResponseEntity<CompletitudDto> recalcularCompletitud(@PathVariable Long id) {
        return ResponseEntity.ok(completitudService.calcular(id));
    }

    // =========================================================================
    // Documentos soporte
    // =========================================================================

    /**
     * Registra un documento soporte para una ESAL.
     * Solo acepta application/pdf - retorna 400 si el contentType no es PDF.
     * Solo ADMINISTRADOR (cubierto por DevSecurityConfig: POST /api/esales/{id}/documentos).
     *
     * @param id             ID de la ESAL
     * @param archivo        archivo multipart a registrar
     * @param tipoProceso    tipo de proceso asociado (opcional)
     * @param tipoDocumento  tipo de documento (opcional)
     * @param authentication autenticacion del usuario en sesion
     * @return DTO con los datos del documento registrado, HTTP 201
     * @throws IOException si ocurre un error de I/O al guardar el archivo
     */
    @PostMapping(value = "/{id}/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Registrar documento soporte",
               description = "Solo acepta application/pdf. Solo ADMINISTRADOR.")
    public ResponseEntity<DocumentoSoporteDto> registrarDocumento(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(value = "tipoProceso", required = false) String tipoProceso,
            @RequestParam(value = "tipoDocumento", required = false) String tipoDocumento,
            Authentication authentication) throws IOException {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        DocumentoSoporteDto result = documentoSoporteService.registrar(
                id,
                archivo.getOriginalFilename(),
                archivo.getContentType(),
                archivo.getSize(),
                archivo.getInputStream(),
                tipoProceso,
                tipoDocumento,
                usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Lista los documentos soporte de una ESAL.
     * ADMINISTRADOR y EXPEDIDOR (cubierto por DevSecurityConfig: GET /api/esales/**).
     *
     * @param id ID de la ESAL
     * @return lista de DTOs con los documentos de la ESAL
     */
    @GetMapping("/{id}/documentos")
    @Operation(summary = "Listar documentos soporte de una ESAL",
               description = "Devuelve todos los documentos soporte asociados a la ESAL.")
    public ResponseEntity<List<DocumentoSoporteDto>> listarDocumentos(@PathVariable Long id) {
        return ResponseEntity.ok(documentoSoporteService.listar(id));
    }
}
