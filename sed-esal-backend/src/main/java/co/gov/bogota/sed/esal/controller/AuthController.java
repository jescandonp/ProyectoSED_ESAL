package co.gov.bogota.sed.esal.controller;

import co.gov.bogota.sed.esal.dto.PermisosDto;
import co.gov.bogota.sed.esal.dto.UsuarioContextoDto;
import co.gov.bogota.sed.esal.service.UsuarioContextoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Informacion del usuario autenticado y permisos efectivos")
public class AuthController {

    private final UsuarioContextoService usuarioContextoService;

    public AuthController(UsuarioContextoService usuarioContextoService) {
        this.usuarioContextoService = usuarioContextoService;
    }

    @GetMapping("/me")
    @Operation(summary = "Retorna el usuario autenticado con email, nombre y rol")
    public ResponseEntity<UsuarioContextoDto> me(Authentication authentication) {
        return ResponseEntity.ok(usuarioContextoService.resolver(authentication));
    }

    @GetMapping("/permisos")
    @Operation(summary = "Retorna los permisos efectivos del usuario autenticado")
    public ResponseEntity<PermisosDto> permisos(Authentication authentication) {
        return ResponseEntity.ok(usuarioContextoService.resolverPermisos(authentication));
    }
}
