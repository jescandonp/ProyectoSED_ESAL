package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.FirmanteCreateDto;
import co.gov.bogota.sed.esal.dto.FirmanteDto;
import co.gov.bogota.sed.esal.service.FirmanteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/firmantes")
public class FirmanteController {

    private final FirmanteService firmanteService;

    public FirmanteController(FirmanteService firmanteService) {
        this.firmanteService = firmanteService;
    }

    @GetMapping
    public ResponseEntity<List<FirmanteDto>> listar() {
        return ResponseEntity.ok(firmanteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FirmanteDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(firmanteService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<FirmanteDto> crear(@RequestBody FirmanteCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(firmanteService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FirmanteDto> actualizar(@PathVariable Long id,
                                                   @RequestBody FirmanteCreateDto dto) {
        return ResponseEntity.ok(firmanteService.actualizar(id, dto));
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<FirmanteDto> activar(@PathVariable Long id) {
        return ResponseEntity.ok(firmanteService.activar(id));
    }

    @PutMapping("/{id}/inactivar")
    public ResponseEntity<FirmanteDto> inactivar(@PathVariable Long id) {
        return ResponseEntity.ok(firmanteService.inactivar(id));
    }
}
