package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.NumeracionDto;
import co.gov.bogota.sed.esal.dto.NumeracionUpdateDto;
import co.gov.bogota.sed.esal.service.NumeracionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/certificados/numeracion")
public class NumeracionController {

    private final NumeracionService numeracionService;

    public NumeracionController(NumeracionService numeracionService) {
        this.numeracionService = numeracionService;
    }

    @GetMapping
    public NumeracionDto obtener() {
        return numeracionService.obtenerActual();
    }

    @PutMapping
    public NumeracionDto actualizar(@RequestBody NumeracionUpdateDto dto, Authentication auth) {
        return numeracionService.actualizarPrefijo(dto, auth.getName());
    }
}
