package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.CambiarEstadoDto;
import co.gov.bogota.sed.esal.dto.CompletitudDto;
import co.gov.bogota.sed.esal.dto.DocumentoSoporteDto;
import co.gov.bogota.sed.esal.dto.EsalInformacionPrincipalDto;
import co.gov.bogota.sed.esal.dto.EsalCreateDto;
import co.gov.bogota.sed.esal.dto.EsalDetalleDto;
import co.gov.bogota.sed.esal.dto.EsalResumenDto;
import co.gov.bogota.sed.esal.dto.EsalUpdateDto;
import co.gov.bogota.sed.esal.dto.MantenimientoEsalDto;
import co.gov.bogota.sed.esal.dto.NombramientoDto;
import co.gov.bogota.sed.esal.dto.OrganoAdministracionDto;
import co.gov.bogota.sed.esal.dto.PageDto;
import co.gov.bogota.sed.esal.dto.PersoneriaJuridicaDto;
import co.gov.bogota.sed.esal.service.CompletitudService;
import co.gov.bogota.sed.esal.service.DocumentoSoporteService;
import co.gov.bogota.sed.esal.service.EsalMaintenanceService;
import co.gov.bogota.sed.esal.service.EsalService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controlador REST para operaciones sobre ESALes.
 *
 * Endpoints CRUD:
 *   GET  /api/esales                             - ADMINISTRADOR y EXPEDIDOR (paginado con filtros)
 *   POST /api/esales                             - solo ADMINISTRADOR
 *   GET  /api/esales/{id}                        - ADMINISTRADOR y EXPEDIDOR
 *   PUT  /api/esales/{id}                        - solo ADMINISTRADOR
 *   PUT  /api/esales/{id}/estado                 - solo ADMINISTRADOR
 *
 * Endpoints de completitud:
 *   GET  /api/esales/{id}/completitud            - ADMINISTRADOR y EXPEDIDOR
 *   POST /api/esales/{id}/completitud/recalcular - solo ADMINISTRADOR
 *
 * Endpoints de documentos soporte:
 *   POST /api/esales/{id}/documentos             - solo ADMINISTRADOR
 *   GET  /api/esales/{id}/documentos             - ADMINISTRADOR y EXPEDIDOR
 *
 * La seguridad esta cubierta por DevSecurityConfig.
 */
@RestController
@RequestMapping("/api/esales")
@Tag(name = "ESALes", description = "Consulta y gestion de ESALes")
@SecurityRequirement(name = "BearerAuth")
public class EsalController {

    private final EsalService esalService;
    private final EsalMaintenanceService esalMaintenanceService;
    private final CompletitudService completitudService;
    private final DocumentoSoporteService documentoSoporteService;

    public EsalController(EsalService esalService,
                          EsalMaintenanceService esalMaintenanceService,
                          CompletitudService completitudService,
                          DocumentoSoporteService documentoSoporteService) {
        this.esalService = esalService;
        this.esalMaintenanceService = esalMaintenanceService;
        this.completitudService = completitudService;
        this.documentoSoporteService = documentoSoporteService;
    }

    // =========================================================================
    // CRUD ESALes
    // =========================================================================

    /**
     * Lista ESALes paginado con filtros opcionales.
     * Accesible por ADMINISTRADOR y EXPEDIDOR.
     *
     * @param page    número de página (0-based, default 0)
     * @param size    tamaño de página (default 20)
     * @param nombre  filtro parcial por nombre (opcional)
     * @param idSipej filtro parcial por idSipej (opcional)
     * @param estado  filtro exacto por estado (opcional)
     * @return página de EsalResumenDto
     */
    @GetMapping
    @Operation(summary = "Listar ESALes paginado",
               description = "Devuelve una página de ESALes con filtros opcionales de nombre, idSipej y estado.")
    public ResponseEntity<PageDto<EsalResumenDto>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String idSipej,
            @RequestParam(required = false) EstadoEsal estado) {
        return ResponseEntity.ok(esalService.listar(page, size, nombre, idSipej, estado));
    }

    /**
     * Crea una nueva ESAL.
     * Solo ADMINISTRADOR.
     *
     * @param dto            datos de la nueva ESAL
     * @param authentication contexto de seguridad del usuario autenticado
     * @return EsalResumenDto con HTTP 201
     */
    @PostMapping
    @Operation(summary = "Crear ESAL",
               description = "Crea una nueva ESAL. Solo ADMINISTRADOR.")
    public ResponseEntity<EsalResumenDto> crear(
            @RequestBody EsalCreateDto dto,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        EsalResumenDto result = esalService.crear(dto, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Obtiene el detalle completo de una ESAL.
     * Accesible por ADMINISTRADOR y EXPEDIDOR.
     *
     * @param id ID de la ESAL
     * @return EsalDetalleDto
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una ESAL",
               description = "Devuelve el detalle completo de una ESAL por su ID.")
    public ResponseEntity<EsalDetalleDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(esalService.obtener(id));
    }

    /**
     * Actualiza los datos de una ESAL.
     * Solo ADMINISTRADOR.
     *
     * @param id             ID de la ESAL
     * @param dto            datos a actualizar
     * @param authentication contexto de seguridad del usuario autenticado
     * @return EsalResumenDto actualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ESAL",
               description = "Actualiza los datos de una ESAL. Solo ADMINISTRADOR.")
    public ResponseEntity<EsalResumenDto> actualizar(
            @PathVariable Long id,
            @RequestBody EsalUpdateDto dto,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        return ResponseEntity.ok(esalService.actualizar(id, dto, usuario));
    }

    /**
     * Cambia el estado de una ESAL.
     * Solo ADMINISTRADOR.
     *
     * @param id             ID de la ESAL
     * @param dto            nuevo estado
     * @param authentication contexto de seguridad del usuario autenticado
     * @return EsalResumenDto con el nuevo estado
     */
    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de una ESAL",
               description = "Cambia el estado administrativo de una ESAL. Solo ADMINISTRADOR.")
    public ResponseEntity<EsalResumenDto> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoDto dto,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        return ResponseEntity.ok(esalService.cambiarEstado(id, dto.getEstado(), usuario));
    }

    // =========================================================================
    // I5 - Mantenimiento seccional
    // =========================================================================

    @PostMapping("/mantenimiento")
    @Operation(summary = "Crear ESAL desde mantenimiento",
               description = "Crea una ESAL para mantenimiento operativo I5. Solo ADMINISTRADOR.")
    public ResponseEntity<MantenimientoEsalDto> crearDesdeMantenimiento(
            @RequestBody EsalInformacionPrincipalDto dto,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        MantenimientoEsalDto result = esalMaintenanceService.crear(dto, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}/mantenimiento")
    @Operation(summary = "Obtener mantenimiento de ESAL",
               description = "Devuelve la vista seccional de mantenimiento I5.")
    public ResponseEntity<MantenimientoEsalDto> obtenerMantenimiento(@PathVariable Long id) {
        return ResponseEntity.ok(esalMaintenanceService.obtenerMantenimiento(id));
    }

    @PutMapping("/{id}/informacion-principal")
    @Operation(summary = "Actualizar informacion principal",
               description = "Actualiza la seccion de informacion principal. Solo ADMINISTRADOR.")
    public ResponseEntity<MantenimientoEsalDto> actualizarInformacionPrincipal(
            @PathVariable Long id,
            @RequestBody EsalInformacionPrincipalDto dto,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        return ResponseEntity.ok(esalMaintenanceService.actualizarInformacionPrincipal(id, dto, usuario));
    }

    @PutMapping("/{id}/personeria-juridica")
    @Operation(summary = "Guardar personeria juridica",
               description = "Crea o actualiza la seccion de personeria juridica. Solo ADMINISTRADOR.")
    public ResponseEntity<MantenimientoEsalDto> guardarPersoneriaJuridica(
            @PathVariable Long id,
            @RequestBody PersoneriaJuridicaDto dto,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        return ResponseEntity.ok(esalMaintenanceService.guardarPersoneriaJuridica(id, dto, usuario));
    }

    @GetMapping("/{id}/representantes")
    @Operation(summary = "Listar representantes legales",
               description = "Lista representantes legales principales y suplentes de la ESAL.")
    public ResponseEntity<List<NombramientoDto>> listarRepresentantes(@PathVariable Long id) {
        return ResponseEntity.ok(esalMaintenanceService.listarRepresentantes(id));
    }

    @PostMapping("/{id}/representantes")
    @Operation(summary = "Crear representante legal",
               description = "Crea representante legal principal o suplente. Solo ADMINISTRADOR.")
    public ResponseEntity<NombramientoDto> crearRepresentante(
            @PathVariable Long id,
            @RequestBody NombramientoDto dto,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        NombramientoDto result = esalMaintenanceService.crearRepresentante(id, dto, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}/representantes/{representanteId}")
    @Operation(summary = "Actualizar representante legal",
               description = "Actualiza representante legal principal o suplente. Solo ADMINISTRADOR.")
    public ResponseEntity<NombramientoDto> actualizarRepresentante(
            @PathVariable Long id,
            @PathVariable Long representanteId,
            @RequestBody NombramientoDto dto,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        return ResponseEntity.ok(esalMaintenanceService.actualizarRepresentante(id, representanteId, dto, usuario));
    }

    @GetMapping("/{id}/organos-administracion")
    @Operation(summary = "Listar organos de administracion",
               description = "Lista miembros del organo de administracion de la ESAL.")
    public ResponseEntity<List<OrganoAdministracionDto>> listarOrganosAdministracion(@PathVariable Long id) {
        return ResponseEntity.ok(esalMaintenanceService.listarMiembrosOrgano(id));
    }

    @PostMapping("/{id}/organos-administracion")
    @Operation(summary = "Crear miembro de organo de administracion",
               description = "Crea un miembro del organo de administracion. Solo ADMINISTRADOR.")
    public ResponseEntity<OrganoAdministracionDto> crearMiembroOrgano(
            @PathVariable Long id,
            @RequestBody OrganoAdministracionDto dto,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        OrganoAdministracionDto result = esalMaintenanceService.crearMiembroOrgano(id, dto, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}/organos-administracion/{miembroId}")
    @Operation(summary = "Actualizar miembro de organo de administracion",
               description = "Actualiza un miembro del organo de administracion. Solo ADMINISTRADOR.")
    public ResponseEntity<OrganoAdministracionDto> actualizarMiembroOrgano(
            @PathVariable Long id,
            @PathVariable Long miembroId,
            @RequestBody OrganoAdministracionDto dto,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        return ResponseEntity.ok(esalMaintenanceService.actualizarMiembroOrgano(id, miembroId, dto, usuario));
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
     * Solo ADMINISTRADOR.
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
     * ADMINISTRADOR y EXPEDIDOR.
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
