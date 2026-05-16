package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.domain.enums.EstadoCompletitud;
import co.gov.bogota.sed.esal.domain.enums.EstadoEsal;
import co.gov.bogota.sed.esal.dto.BusquedaDetalleDto;
import co.gov.bogota.sed.esal.dto.BusquedaResultadoDto;
import co.gov.bogota.sed.esal.dto.PageDto;
import co.gov.bogota.sed.esal.service.BusquedaService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/busquedas")
public class BusquedaController {

    private final BusquedaService busquedaService;

    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @GetMapping("/esales")
    public PageDto<BusquedaResultadoDto> buscarEsales(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String idSipej,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String nit,
            @RequestParam(required = false) EstadoEsal estado,
            @RequestParam(required = false) EstadoCompletitud estadoCompletitud,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        return busquedaService.buscar(q, idSipej, nombre, nit, estado, estadoCompletitud, page, size, usuario);
    }

    @GetMapping("/esales/{id}")
    public BusquedaDetalleDto obtenerDetalle(@PathVariable Long id, Authentication authentication) {
        String usuario = authentication != null ? authentication.getName() : "sistema";
        return busquedaService.obtenerDetalle(id, usuario);
    }
}
