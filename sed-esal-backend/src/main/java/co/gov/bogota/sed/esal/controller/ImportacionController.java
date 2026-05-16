package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.EsalImportResultDto;
import co.gov.bogota.sed.esal.service.AuditoriaAcciones;
import co.gov.bogota.sed.esal.service.AuditoriaService;
import co.gov.bogota.sed.esal.service.EsalImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controlador para importación histórica de ESALes.
 * Solo accesible por ADMINISTRADOR (protegido por DevSecurityConfig).
 */
@RestController
@RequestMapping("/api/admin/importaciones")
@Tag(name = "Importaciones", description = "Importación histórica de ESALes desde Excel")
@SecurityRequirement(name = "BearerAuth")
public class ImportacionController {

    private final EsalImportService esalImportService;
    private final AuditoriaService auditoriaService;

    public ImportacionController(EsalImportService esalImportService,
                                  AuditoriaService auditoriaService) {
        this.esalImportService = esalImportService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Importa ESALes desde un archivo Excel (.xlsx).
     * Requiere rol ADMINISTRADOR.
     *
     * @param archivo        archivo Excel BASE DE DATOS - REGISTRO_1.xlsx
     * @param authentication contexto de seguridad del usuario autenticado
     * @return resumen de la importación
     */
    @PostMapping("/esal")
    @Operation(summary = "Importar base histórica de ESALes",
               description = "Lee el archivo Excel y persiste las ESALes con sus datos relacionados.")
    public ResponseEntity<EsalImportResultDto> importarEsal(
            @RequestParam("archivo") MultipartFile archivo,
            Authentication authentication) throws IOException {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        String rol = auditoriaService.obtenerRolActual();
        EsalImportResultDto result = esalImportService.importar(archivo.getInputStream(), usuario);

        String detalle = "Importados: " + result.getTotalImportados()
                + ", Rechazados: " + result.getTotalRechazados()
                + ", Advertencias: " + result.getTotalAdvertencias();
        auditoriaService.registrar(usuario, rol,
                AuditoriaAcciones.IMPORTAR_ESAL,
                AuditoriaAcciones.ENTIDAD_IMPORTACION,
                result.getImportacionId(), null,
                AuditoriaAcciones.RESULTADO_EXITO, detalle);

        return ResponseEntity.ok(result);
    }
}
