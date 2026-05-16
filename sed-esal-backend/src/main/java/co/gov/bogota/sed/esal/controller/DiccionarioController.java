package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.DiccionarioImportResultDto;
import co.gov.bogota.sed.esal.service.DiccionarioImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Endpoint administrativo para inicializar el diccionario de campos
 * desde el archivo Base excel.xlsx.
 *
 * Solo accesible por usuarios con rol ADMINISTRADOR.
 */
@RestController
@RequestMapping("/api/admin/diccionario")
@Tag(name = "Diccionario", description = "Gestión del diccionario de obligatoriedad de campos")
@SecurityRequirement(name = "BearerAuth")
public class DiccionarioController {

    private final DiccionarioImportService service;

    public DiccionarioController(DiccionarioImportService service) {
        this.service = service;
    }

    /**
     * Inicializa (o reinicializa) el diccionario de campos desde un archivo Excel.
     * La operación es idempotente: elimina los registros existentes y recarga.
     *
     * @param archivo archivo Base excel.xlsx enviado como multipart/form-data
     * @return resumen de la importación con conteos y advertencias
     */
    @PostMapping("/inicializar")
    @Operation(
            summary = "Inicializar diccionario de campos",
            description = "Lee Base excel.xlsx y persiste las 117 definiciones de obligatoriedad. "
                    + "Operación idempotente: elimina registros previos antes de recargar."
    )
    public ResponseEntity<DiccionarioImportResultDto> inicializar(
            @RequestParam("archivo") MultipartFile archivo) throws IOException {
        DiccionarioImportResultDto result = service.importar(archivo.getInputStream());
        return ResponseEntity.ok(result);
    }
}
