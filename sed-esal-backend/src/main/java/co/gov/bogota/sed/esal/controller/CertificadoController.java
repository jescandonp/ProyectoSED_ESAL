package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.CertificadoDto;
import co.gov.bogota.sed.esal.service.GeneracionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    private final GeneracionService generacionService;

    public CertificadoController(GeneracionService generacionService) {
        this.generacionService = generacionService;
    }

    @PostMapping("/esales/{id}/generar")
    public CertificadoDto generar(@PathVariable Long id, Authentication auth) {
        return generacionService.generar(id, auth.getName());
    }

    @GetMapping("/{certificadoId}")
    public CertificadoDto obtener(@PathVariable Long certificadoId) {
        return generacionService.obtener(certificadoId);
    }

    @GetMapping("/{certificadoId}/descargar")
    public ResponseEntity<byte[]> descargar(@PathVariable Long certificadoId, Authentication auth) {
        CertificadoDto cert = generacionService.obtener(certificadoId);
        byte[] bytes = generacionService.descargar(certificadoId, auth.getName());
        String nombre = cert.getNombreArchivo() != null ? cert.getNombreArchivo() : "certificado.pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    @GetMapping("/esales/{id}/historial")
    public List<CertificadoDto> historial(@PathVariable Long id) {
        return generacionService.listarPorEsal(id);
    }
}
