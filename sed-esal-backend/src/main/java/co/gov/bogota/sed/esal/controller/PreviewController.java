package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.PreviewCertificadoDto;
import co.gov.bogota.sed.esal.service.PreviewService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificados/preview")
public class PreviewController {

    private final PreviewService previewService;

    public PreviewController(PreviewService previewService) {
        this.previewService = previewService;
    }

    @GetMapping("/esales/{id}")
    public PreviewCertificadoDto obtenerPreview(@PathVariable Long id, Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        return previewService.obtenerPreview(id, usuario);
    }
}
