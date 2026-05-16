package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.FirmanteCreateDto;
import co.gov.bogota.sed.esal.dto.FirmanteDto;
import co.gov.bogota.sed.esal.service.FirmanteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
    public List<FirmanteDto> listar() {
        return firmanteService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FirmanteDto crear(@RequestBody FirmanteCreateDto dto, Authentication auth) {
        return firmanteService.crear(dto, auth.getName());
    }

    @PutMapping("/{id}")
    public FirmanteDto actualizar(@PathVariable Long id,
                                   @RequestBody FirmanteCreateDto dto,
                                   Authentication auth) {
        return firmanteService.actualizar(id, dto, auth.getName());
    }

    @PutMapping("/{id}/activar")
    public FirmanteDto activar(@PathVariable Long id, Authentication auth) {
        return firmanteService.activar(id, auth.getName());
    }

    @PutMapping("/{id}/inactivar")
    public FirmanteDto inactivar(@PathVariable Long id, Authentication auth) {
        return firmanteService.inactivar(id, auth.getName());
    }
}
