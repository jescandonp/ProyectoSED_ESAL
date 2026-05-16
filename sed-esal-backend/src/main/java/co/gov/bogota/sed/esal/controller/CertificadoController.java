package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.CertificadoDto;
import co.gov.bogota.sed.esal.service.GeneracionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    private final GeneracionService generacionService;

    public CertificadoController(GeneracionService generacionService) {
        this.generacionService = generacionService;
    }

    /** Genera un nuevo certificado para la ESAL indicada. */
    @PostMapping("/esales/{esalId}/generar")
    public ResponseEntity<CertificadoDto> generar(@PathVariable Long esalId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(generacionService.generar(esalId));
    }

    /** Consulta un certificado por su ID. */
    @GetMapping("/{certificadoId}")
    public ResponseEntity<CertificadoDto> obtener(@PathVariable Long certificadoId) {
        return ResponseEntity.ok(generacionService.obtener(certificadoId));
    }

    /** Descarga el PDF del certificado con validación de hash. */
    @GetMapping("/{certificadoId}/descargar")
    public ResponseEntity<byte[]> descargar(@PathVariable Long certificadoId) {
        byte[] pdf = generacionService.descargar(certificadoId);
        CertificadoDto meta = generacionService.obtener(certificadoId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                meta.getNombreArchivo() != null ? meta.getNombreArchivo() : "certificado.pdf");
        headers.setContentLength(pdf.length);
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    /** Historial de certificados expedidos para una ESAL. */
    @GetMapping("/esales/{esalId}/historial")
    public ResponseEntity<List<CertificadoDto>> historial(@PathVariable Long esalId) {
        return ResponseEntity.ok(generacionService.historialPorEsal(esalId));
    }
}
