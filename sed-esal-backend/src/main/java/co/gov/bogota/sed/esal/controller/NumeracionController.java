package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.NumeracionDto;
import co.gov.bogota.sed.esal.dto.NumeracionUpdateDto;
import co.gov.bogota.sed.esal.service.NumeracionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/certificados/numeracion")
public class NumeracionController {

    private final NumeracionService numeracionService;

    public NumeracionController(NumeracionService numeracionService) {
        this.numeracionService = numeracionService;
    }

    @GetMapping
    public ResponseEntity<NumeracionDto> consultar() {
        return ResponseEntity.ok(numeracionService.consultar());
    }

    @PutMapping
    public ResponseEntity<NumeracionDto> actualizar(@RequestBody NumeracionUpdateDto dto) {
        return ResponseEntity.ok(numeracionService.actualizar(dto));
    }
}
